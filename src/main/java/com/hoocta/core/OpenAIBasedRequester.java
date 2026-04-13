package com.hoocta.core;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hoocta.core.config.LLMEndpoint;
import com.hoocta.core.config.LLMRequestOptions;
import com.hoocta.http.ServiceHttpResponse;
import com.hoocta.http.ServiceRequester;
import com.hoocta.utils.JsonUtils;
import com.hoocta.utils.LogUtils;
import com.hoocta.utils.StringUtils;

/**
 * OpenAI 请求模板
 * @author roderickyu Jul 25, 2024
 */
public class OpenAIBasedRequester {

    protected static OpenAIResponseSuccessResult complete(LLMEndpoint endpoint, List<Message> messages, String conversationId, LLMRequestOptions options, boolean longTimeoutClient) throws ServiceException {
        if (endpoint.getApiFormat() != LLMEndpoint.ApiFormat.OPENAI_CHAT_COMPLETIONS) {
            return completeNative(endpoint, messages, conversationId, options, longTimeoutClient);
        }
        if (messages == null || messages.isEmpty()) {
            LogUtils.log("Empty messages when calling OpenAI API. Conversation: " + conversationId);
            return null;
        }
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json; charset=UTF-8");
        endpoint.apiKeyOptional().ifPresent(key -> headers.put("Authorization", "Bearer " + key));
        headers.putAll(endpoint.getHeaders());

        OpenAIParams params = new OpenAIParams();
        params.setModel(endpoint.getModel());
        params.setMessages(messages);

        if (options != null) {
            if (options.getTemperature() != null) {
                params.setTemperature(options.getTemperature());
            }
            if (options.getMaxTokens() != null) {
                params.setMaxTokens(options.getMaxTokens());
            }
            if (options.getTopP() != null) {
                params.setTopP(options.getTopP());
            }
            if (options.getPresencePenalty() != null) {
                params.setPresencePenalty(options.getPresencePenalty());
            }
            if (options.getFrequencyPenalty() != null) {
                params.setFrequencyPenalty(options.getFrequencyPenalty());
            }
        }
        boolean stream = options != null && Boolean.TRUE.equals(options.getStream());
        params.setStream(stream);
        if (stream) {
            StreamOptions streamOptions = new StreamOptions();
            streamOptions.setInclude_usage(true);
            params.setStream_options(streamOptions);
        }
        ServiceHttpResponse response = ServiceRequester.post(endpoint.getBaseUrl(), headers, null, JsonUtils.toJson(params), longTimeoutClient, stream);
        String responseContent = response.getResponseContent();
        if (response.getStatus() < 200 || response.getStatus() >= 300) {
            throw new ServiceException(com.hoocta.llm.constants.BizCode.SERVER_BUSY,
                    "OpenAI-compatible LLM API failed status=" + response.getStatus()
                            + ", endpoint=" + endpoint
                            + ", body=" + responseContent
                            + ", conversationId=" + conversationId);
        }
        if (StringUtils.isBlank(responseContent)) {
            LogUtils.log("Response Null or Empty value: " + responseContent + ". Conversation Id: " + conversationId);
            throw new ServiceException(com.hoocta.llm.constants.BizCode.SERVER_BUSY,
                    "OpenAI-compatible LLM API returned empty response, status=" + response.getStatus()
                            + ", endpoint=" + endpoint
                            + ", conversationId=" + conversationId);
        }
        LogUtils.log("Raw response(" + conversationId + "): " + responseContent);
        OpenAIResponseResult result = OpenAIResponseResult.buildResult(responseContent, stream);
        if (result instanceof OpenAIErrorResult) {
            OpenAIErrorResult errorResult = (OpenAIErrorResult) result;
            LogUtils.log("API Error: " + result + ". Conversation Id: " + conversationId);
            throw new ServiceException(com.hoocta.llm.constants.BizCode.SERVER_BUSY, "ConversationId: " + conversationId + ", Api result: " + errorResult);
        }

        return (OpenAIResponseSuccessResult) result;
    }

    private static OpenAIResponseSuccessResult completeNative(LLMEndpoint endpoint, List<Message> messages, String conversationId, LLMRequestOptions options, boolean longTimeoutClient) throws ServiceException {
        if (messages == null || messages.isEmpty()) {
            LogUtils.log("Empty messages when calling native LLM API. Conversation: " + conversationId);
            return null;
        }
        if (options != null && Boolean.TRUE.equals(options.getStream())) {
            LogUtils.log("Native LLM endpoint ignores stream=true and uses synchronous response mode. ConversationId: " + conversationId);
        }

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json; charset=UTF-8");
        endpoint.apiKeyOptional().ifPresent(key -> headers.put("Authorization", "Bearer " + key));
        headers.putAll(endpoint.getHeaders());

        String requestBody;
        if (endpoint.getApiFormat() == LLMEndpoint.ApiFormat.GOOGLE_NATIVE_GENERATE_CONTENT) {
            requestBody = buildGeminiNativeBody(messages, options);
        }
        else if (endpoint.getApiFormat() == LLMEndpoint.ApiFormat.CLAUDE_MESSAGES) {
            requestBody = buildClaudeMessagesBody(endpoint, messages, options);
        }
        else {
            throw new ServiceException(com.hoocta.llm.constants.BizCode.SERVER_BUSY, "Unsupported native API format: " + endpoint.getApiFormat());
        }

        ServiceHttpResponse response = ServiceRequester.post(endpoint.getBaseUrl(), headers, null, requestBody, longTimeoutClient, false);
        String responseContent = response.getResponseContent();
        if (response.getStatus() < 200 || response.getStatus() >= 300) {
            throw new ServiceException(com.hoocta.llm.constants.BizCode.SERVER_BUSY,
                    "Native LLM API failed status=" + response.getStatus()
                            + ", endpoint=" + endpoint
                            + ", body=" + responseContent
                            + ", conversationId=" + conversationId);
        }
        if (StringUtils.isBlank(responseContent)) {
            LogUtils.log("Response Null or Empty value: " + responseContent + ". Conversation Id: " + conversationId);
            throw new ServiceException(com.hoocta.llm.constants.BizCode.SERVER_BUSY,
                    "Native LLM API returned empty response, status=" + response.getStatus()
                            + ", endpoint=" + endpoint
                            + ", conversationId=" + conversationId);
        }
        LogUtils.log("Raw response(" + conversationId + "): " + responseContent);

        if (endpoint.getApiFormat() == LLMEndpoint.ApiFormat.GOOGLE_NATIVE_GENERATE_CONTENT) {
            return parseGeminiNativeResponse(endpoint, responseContent);
        }
        return parseClaudeMessagesResponse(endpoint, responseContent);
    }

    private static String buildGeminiNativeBody(List<Message> messages, LLMRequestOptions options) {
        JsonObject body = new JsonObject();
        JsonArray contents = new JsonArray();

        StringBuilder systemText = new StringBuilder();
        for (Message message : messages) {
            if (message == null || StringUtils.isBlank(message.getContent())) {
                continue;
            }
            if (LLMRole.SYSTEM.getName().equals(message.getRole())) {
                if (!systemText.isEmpty()) {
                    systemText.append("\n\n");
                }
                systemText.append(message.getContent());
                continue;
            }
            JsonObject content = new JsonObject();
            content.addProperty("role", LLMRole.ASISTANT.getName().equals(message.getRole()) ? "model" : "user");
            JsonArray parts = new JsonArray();
            JsonObject textPart = new JsonObject();
            textPart.addProperty("text", message.getContent());
            parts.add(textPart);
            content.add("parts", parts);
            contents.add(content);
        }
        if (!systemText.isEmpty()) {
            JsonObject systemInstruction = new JsonObject();
            JsonArray parts = new JsonArray();
            JsonObject textPart = new JsonObject();
            textPart.addProperty("text", systemText.toString());
            parts.add(textPart);
            systemInstruction.add("parts", parts);
            body.add("systemInstruction", systemInstruction);
        }
        body.add("contents", contents);

        JsonObject generationConfig = new JsonObject();
        if (options != null) {
            if (options.getTemperature() != null) {
                generationConfig.addProperty("temperature", options.getTemperature());
            }
            if (options.getMaxTokens() != null) {
                generationConfig.addProperty("maxOutputTokens", options.getMaxTokens());
            }
            if (options.getTopP() != null) {
                generationConfig.addProperty("topP", options.getTopP());
            }
        }
        if (!generationConfig.entrySet().isEmpty()) {
            body.add("generationConfig", generationConfig);
        }
        return JsonUtils.toJson(body);
    }

    private static String buildClaudeMessagesBody(LLMEndpoint endpoint, List<Message> messages, LLMRequestOptions options) {
        JsonObject body = new JsonObject();
        body.addProperty("model", endpoint.getModel());
        int maxTokens = options != null && options.getMaxTokens() != null ? options.getMaxTokens() : 1024;
        body.addProperty("max_tokens", maxTokens);

        StringBuilder systemText = new StringBuilder();
        JsonArray claudeMessages = new JsonArray();
        for (Message message : messages) {
            if (message == null || StringUtils.isBlank(message.getContent())) {
                continue;
            }
            if (LLMRole.SYSTEM.getName().equals(message.getRole())) {
                if (!systemText.isEmpty()) {
                    systemText.append("\n\n");
                }
                systemText.append(message.getContent());
                continue;
            }
            JsonObject claudeMessage = new JsonObject();
            claudeMessage.addProperty("role", LLMRole.ASISTANT.getName().equals(message.getRole()) ? "assistant" : "user");
            claudeMessage.addProperty("content", message.getContent());
            claudeMessages.add(claudeMessage);
        }
        if (!systemText.isEmpty()) {
            body.addProperty("system", systemText.toString());
        }
        body.add("messages", claudeMessages);
        if (options != null) {
            if (options.getTemperature() != null) {
                body.addProperty("temperature", options.getTemperature());
            }
            if (options.getTopP() != null) {
                body.addProperty("top_p", options.getTopP());
            }
        }
        return JsonUtils.toJson(body);
    }

    private static OpenAIResponseSuccessResult parseGeminiNativeResponse(LLMEndpoint endpoint, String responseContent) throws ServiceException {
        JsonObject response = JsonParser.parseString(responseContent).getAsJsonObject();
        if (response.has("error")) {
            throw new ServiceException(com.hoocta.llm.constants.BizCode.SERVER_BUSY, "Gemini native API error: " + responseContent);
        }
        JsonArray candidates = response.getAsJsonArray("candidates");
        if (candidates == null || candidates.isEmpty()) {
            throw new ServiceException(com.hoocta.llm.constants.BizCode.SERVER_BUSY, "Gemini native API returned no candidates: " + responseContent);
        }
        JsonObject firstCandidate = candidates.get(0).getAsJsonObject();
        JsonObject content = firstCandidate.getAsJsonObject("content");
        String text = extractGeminiText(content);
        String finishReason = firstCandidate.has("finishReason") ? firstCandidate.get("finishReason").getAsString() : null;

        OpenAIResponseSuccessResult result = new OpenAIResponseSuccessResult();
        result.setModel(endpoint.getModel());
        result.setUsage(extractGeminiUsage(response));
        result.setChoices(List.of(buildChoice(text, mapGeminiFinishReason(finishReason))));
        return result;
    }

    private static OpenAIResponseSuccessResult parseClaudeMessagesResponse(LLMEndpoint endpoint, String responseContent) throws ServiceException {
        JsonObject response = JsonParser.parseString(responseContent).getAsJsonObject();
        if (response.has("error")) {
            throw new ServiceException(com.hoocta.llm.constants.BizCode.SERVER_BUSY, "Claude Messages API error: " + responseContent);
        }
        JsonArray content = response.getAsJsonArray("content");
        if (content == null || content.isEmpty()) {
            throw new ServiceException(com.hoocta.llm.constants.BizCode.SERVER_BUSY, "Claude Messages API returned no content: " + responseContent);
        }
        StringBuilder text = new StringBuilder();
        for (JsonElement blockElement : content) {
            JsonObject block = blockElement.getAsJsonObject();
            if (block.has("type") && "text".equals(block.get("type").getAsString()) && block.has("text")) {
                text.append(block.get("text").getAsString());
            }
        }
        String stopReason = response.has("stop_reason") && !response.get("stop_reason").isJsonNull() ? response.get("stop_reason").getAsString() : null;

        OpenAIResponseSuccessResult result = new OpenAIResponseSuccessResult();
        result.setId(response.has("id") ? response.get("id").getAsString() : null);
        result.setModel(response.has("model") ? response.get("model").getAsString() : endpoint.getModel());
        result.setUsage(extractClaudeUsage(response));
        result.setChoices(List.of(buildChoice(text.toString(), mapClaudeFinishReason(stopReason))));
        return result;
    }

    private static String extractGeminiText(JsonObject content) {
        if (content == null || !content.has("parts")) {
            return "";
        }
        StringBuilder text = new StringBuilder();
        for (JsonElement partElement : content.getAsJsonArray("parts")) {
            JsonObject part = partElement.getAsJsonObject();
            if (part.has("text")) {
                text.append(part.get("text").getAsString());
            }
        }
        return text.toString();
    }

    private static Usage extractGeminiUsage(JsonObject response) {
        Usage usage = new Usage();
        JsonObject metadata = response.getAsJsonObject("usageMetadata");
        if (metadata == null) {
            return usage;
        }
        usage.setPromptTokens(getInt(metadata, "promptTokenCount"));
        usage.setCompletionTokens(getInt(metadata, "candidatesTokenCount"));
        usage.setTotalTokens(getInt(metadata, "totalTokenCount"));
        return usage;
    }

    private static Usage extractClaudeUsage(JsonObject response) {
        Usage usage = new Usage();
        JsonObject rawUsage = response.getAsJsonObject("usage");
        if (rawUsage == null) {
            return usage;
        }
        usage.setPromptTokens(getInt(rawUsage, "input_tokens"));
        usage.setCompletionTokens(getInt(rawUsage, "output_tokens"));
        usage.setTotalTokens(usage.getPromptTokens() + usage.getCompletionTokens());
        return usage;
    }

    private static Choice buildChoice(String content, String finishReason) {
        Choice choice = new Choice();
        choice.setIndex(0);
        choice.setFinishReason(finishReason);
        choice.setMessage(Message.buildMessage(LLMRole.ASISTANT, content));
        return choice;
    }

    private static String mapGeminiFinishReason(String finishReason) {
        if ("STOP".equals(finishReason)) {
            return OpenAIFinishReason.STOP.getReason();
        }
        return finishReason == null ? OpenAIFinishReason.STOP.getReason() : finishReason.toLowerCase();
    }

    private static String mapClaudeFinishReason(String stopReason) {
        if ("end_turn".equals(stopReason) || "stop_sequence".equals(stopReason)) {
            return OpenAIFinishReason.STOP.getReason();
        }
        return stopReason == null ? OpenAIFinishReason.STOP.getReason() : stopReason;
    }

    private static int getInt(JsonObject object, String member) {
        JsonElement value = object.get(member);
        if (value == null || value.isJsonNull()) {
            return 0;
        }
        return value.getAsInt();
    }
}
