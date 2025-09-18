package com.hoocta.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.hoocta.core.common.IResultParser;
import com.hoocta.core.config.LLMClientConfiguration;
import com.hoocta.core.config.LLMEndpoint;
import com.hoocta.core.config.LLMRequestOptions;
import com.hoocta.core.config.LLMClientConfiguration.Tier;
import com.hoocta.utils.JsonUtils;
import com.hoocta.utils.LogUtils;
import com.hoocta.utils.StringUtils;

/**
 * High-level client that manages retries, failover, and response parsing for OpenAI-compatible
 * chat completions.
 */
public final class LLMClient {

    private final LLMClientConfiguration configuration;
    private final int maxAttemptsPerEndpoint;
    private final Map<String, Usage> usageByModel;

    private LLMClient(Builder builder) {
        this.configuration = Objects.requireNonNull(builder.configuration, "configuration");
        this.maxAttemptsPerEndpoint = builder.maxAttemptsPerEndpoint;
        this.usageByModel = builder.trackUsage ? new ConcurrentHashMap<>() : Collections.emptyMap();
    }

    public static Builder builder(LLMClientConfiguration configuration) {
        return new Builder(configuration);
    }

    public <T> T complete(String userPrompt, IResultParser<T> parser) {
        return complete(null, userPrompt, parser, Tier.STANDARD, null);
    }

    public <T> T complete(String systemPrompt, String userPrompt, IResultParser<T> parser) {
        return complete(systemPrompt, userPrompt, parser, Tier.STANDARD, null);
    }

    public <T> T complete(String systemPrompt, String userPrompt, IResultParser<T> parser, boolean useSmartModel) {
        return complete(systemPrompt, userPrompt, parser, useSmartModel ? Tier.SMART : Tier.STANDARD, null);
    }

    public <T> T complete(String systemPrompt, String userPrompt, IResultParser<T> parser, boolean useSmartModel, LLMRequestOptions overrides) {
        return complete(systemPrompt, userPrompt, parser, useSmartModel ? Tier.SMART : Tier.STANDARD, overrides);
    }

    public <T> T complete(String systemPrompt, String userPrompt, IResultParser<T> parser, Tier tier, LLMRequestOptions overrides) {
        Message promptMessage = Message.buildMessage(LLMRole.USER, userPrompt);
        return complete(systemPrompt, List.of(promptMessage), parser, tier, overrides);
    }

    public <T> T complete(String systemPrompt, List<Message> messages, IResultParser<T> parser) {
        return complete(systemPrompt, messages, parser, Tier.STANDARD, null);
    }

    public <T> T complete(String systemPrompt, List<Message> messages, IResultParser<T> parser, boolean useSmartModel, LLMRequestOptions overrides) {
        return complete(systemPrompt, messages, parser, useSmartModel ? Tier.SMART : Tier.STANDARD, overrides);
    }

    public <T> T complete(String systemPrompt, List<Message> messages, IResultParser<T> parser, Tier tier, LLMRequestOptions overrides) {
        Objects.requireNonNull(parser, "parser");
        List<Message> immutableMessages = messages == null ? List.of() : List.copyOf(messages);
        List<LLMEndpoint> endpoints = selectEndpoints(tier);
        if (endpoints.isEmpty()) {
            throw new IllegalStateException("No LLM endpoints configured for tier " + tier);
        }

        for (LLMEndpoint endpoint : endpoints) {
            T value = tryEndpoint(endpoint, systemPrompt, immutableMessages, parser, overrides);
            if (value != null) {
                return value;
            }
        }
        return null;
    }

    public Map<String, Usage> usageSnapshot() {
        if (usageByModel.isEmpty()) {
            return Map.of();
        }
        Map<String, Usage> snapshot = new ConcurrentHashMap<>();
        usageByModel.forEach((model, usage) -> snapshot.put(model, copyUsage(usage)));
        return Collections.unmodifiableMap(snapshot);
    }

    private <T> T tryEndpoint(LLMEndpoint endpoint, String systemPrompt, List<Message> messages, IResultParser<T> parser, LLMRequestOptions overrides) {
        String conversationId = UUID.randomUUID().toString();
        List<Message> conversation = buildConversation(systemPrompt, messages);
        LLMRequestOptions effectiveOptions = resolveOptions(endpoint, overrides);

        LogUtils.log("LLMClient invoking endpoint " + endpoint + " (conversationId=" + conversationId + ") with messages: " + JsonUtils.toJson(conversation));

        for (int attempt = 1; attempt <= maxAttemptsPerEndpoint; attempt++) {
            if (attempt > 1) {
                LogUtils.log("Retrying endpoint " + endpoint + " (conversationId=" + conversationId + ", attempt=" + attempt + ")");
            }
            try {
                OpenAIResponseSuccessResult response = OpenAIRequestProxy.complete(endpoint, conversation, conversationId, effectiveOptions);
                if (response == null) {
                    LogUtils.log("Endpoint returned null response, will retry (conversationId=" + conversationId + ")");
                    continue;
                }
                recordUsage(response);
                String content = OpenAIResponseResultHelper.getCompleteStopResult(response);
                if (StringUtils.isBlank(content)) {
                    LogUtils.log("Finish reason not STOP or empty content, retrying (conversationId=" + conversationId + ")");
                    continue;
                }
                T parsed = parser.parseExpectedResult(conversationId, content);
                if (parsed == null) {
                    LogUtils.log("Parser returned null, retrying (conversationId=" + conversationId + ")");
                    continue;
                }
                return parsed;
            } catch (Exception ex) {
                LogUtils.log("Error invoking endpoint " + endpoint + " (conversationId=" + conversationId + "): " + ex.getMessage());
                if (shouldStopRetry(ex)) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
        return null;
    }

    private boolean shouldStopRetry(Exception ex) {
        if (ex instanceof InterruptedException) {
            return true;
        }
        Throwable cause = ex.getCause();
        while (cause != null) {
            if (cause instanceof InterruptedException) {
                return true;
            }
            cause = cause.getCause();
        }
        return false;
    }

    private void recordUsage(OpenAIResponseSuccessResult response) {
        if (usageByModel.isEmpty()) {
            return;
        }
        Usage reported = response.getUsage();
        if (reported == null) {
            return;
        }
        String model = response.getModel();
        if (StringUtils.isBlank(model)) {
            model = "unknown-model";
        }
        Usage increment = copyUsage(reported);
        usageByModel.merge(model, increment, (existing, inc) -> {
            existing.setPromptTokens(existing.getPromptTokens() + inc.getPromptTokens());
            existing.setCompletionTokens(existing.getCompletionTokens() + inc.getCompletionTokens());
            existing.setTotalTokens(existing.getTotalTokens() + inc.getTotalTokens());
            return existing;
        });
    }

    private Usage copyUsage(Usage usage) {
        Usage copy = new Usage();
        copy.setPromptTokens(usage.getPromptTokens());
        copy.setCompletionTokens(usage.getCompletionTokens());
        copy.setTotalTokens(usage.getTotalTokens());
        return copy;
    }

    private LLMRequestOptions resolveOptions(LLMEndpoint endpoint, LLMRequestOptions overrides) {
        LLMRequestOptions result = configuration.defaultOptions();
        if (endpoint.getDefaultOptions().isPresent()) {
            result = mergeOptions(result, endpoint.getDefaultOptions().get());
        }
        result = mergeOptions(result, overrides);
        return result;
    }

    private LLMRequestOptions mergeOptions(LLMRequestOptions base, LLMRequestOptions overrides) {
        if (overrides == null) {
            return base;
        }
        if (base == null) {
            return overrides;
        }
        return base.merge(overrides);
    }

    private List<Message> buildConversation(String systemPrompt, List<Message> messages) {
        LinkedList<Message> conversation = new LinkedList<>();
        if (!StringUtils.isBlank(systemPrompt)) {
            conversation.add(Message.buildMessage(LLMRole.SYSTEM, systemPrompt));
        }
        conversation.addAll(messages);
        return conversation;
    }

    private List<LLMEndpoint> selectEndpoints(Tier tier) {
        List<LLMEndpoint> endpoints = new ArrayList<>(configuration.endpoints(tier));
        if (endpoints.isEmpty() && tier == Tier.SMART) {
            endpoints = new ArrayList<>(configuration.endpoints(Tier.STANDARD));
        }
        return endpoints;
    }

    public static final class Builder {
        private final LLMClientConfiguration configuration;
        private int maxAttemptsPerEndpoint = 5;
        private boolean trackUsage = true;

        private Builder(LLMClientConfiguration configuration) {
            this.configuration = configuration;
        }

        public Builder maxAttemptsPerEndpoint(int attempts) {
            if (attempts <= 0) {
                throw new IllegalArgumentException("maxAttemptsPerEndpoint must be > 0");
            }
            this.maxAttemptsPerEndpoint = attempts;
            return this;
        }

        public Builder trackUsage(boolean trackUsage) {
            this.trackUsage = trackUsage;
            return this;
        }

        public LLMClient build() {
            return new LLMClient(this);
        }
    }
}
