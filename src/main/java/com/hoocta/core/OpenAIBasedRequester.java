package com.hoocta.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        if (StringUtils.isBlank(responseContent)) {
            LogUtils.log("Response Null or Empty value: " + responseContent + ". Conversation Id: " + conversationId);
            throw new ServiceException(com.hoocta.llm.constants.BizCode.SERVER_BUSY);
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
}
