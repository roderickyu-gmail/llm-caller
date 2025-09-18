package com.hoocta.core;

import java.util.List;

import com.hoocta.core.config.LLMEndpoint;
import com.hoocta.core.config.LLMRequestOptions;

/**
 * Adapter for issuing OpenAI-compatible completion requests using a configured endpoint.
 */
public final class OpenAIRequestProxy extends OpenAIBasedRequester {

    private OpenAIRequestProxy() {
    }

    public static OpenAIResponseSuccessResult complete(LLMEndpoint endpoint, List<Message> messages, String conversationId, LLMRequestOptions options) throws ServiceException {
        return complete(endpoint, messages, conversationId, options, endpoint.isLongTimeout());
    }
}
