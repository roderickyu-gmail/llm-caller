package com.hoocta.core.common;

import java.util.Objects;

import com.hoocta.core.LLMClient;
import com.hoocta.core.LLMClientRegistry;
import com.hoocta.core.config.LLMRequestOptions;
import com.hoocta.llm.constants.AITags;

/**
 * 固定响应格式的 AI 请求器
 * @author roderickyu Jan 2, 2025
 */
public abstract class AbstractFixedFormatAIRequester<T> {

    private final LLMClient llmClient;

    protected AbstractFixedFormatAIRequester() {
        this(LLMClientRegistry.getRequired());
    }

    protected AbstractFixedFormatAIRequester(LLMClient llmClient) {
        this.llmClient = Objects.requireNonNull(llmClient, "llmClient");
    }

    protected LLMClient client() {
        return llmClient;
    }

    public T complete(String systemPrompt, String userPrompt) {
        return complete(systemPrompt, userPrompt, false);
    }

    public T complete(String systemPrompt, String userPrompt, boolean useSmartModel) {
        return complete(systemPrompt, userPrompt, useSmartModel, true);
    }

    public T complete(String systemPrompt, String userPrompt, boolean useSmartModel, boolean stream) {
        LLMRequestOptions options = stream ? LLMRequestOptions.builder().stream(true).build() : null;
        return complete(systemPrompt, userPrompt, useSmartModel, options);
    }

    public T complete(String systemPrompt, String userPrompt, boolean useSmartModel, LLMRequestOptions options) {
        String prompt = userPrompt + getResultPositionRequirement();
        return llmClient.complete(systemPrompt, prompt, getParser(), useSmartModel, options);
    }

    public abstract IResultParser<T> getParser();

    public String getResultPositionRequirement() {
        return "。将最终结果放到" + AITags.BEGIN_TAG + AITags.END_TAG + "内返回。";
    }
}
