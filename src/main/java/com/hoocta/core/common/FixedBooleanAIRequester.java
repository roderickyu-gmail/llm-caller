package com.hoocta.core.common;

import com.hoocta.core.LLMClient;

/**
 * 简单判断，回答是/否
 * @author roderickyu Jul 29, 2024
 */
public class FixedBooleanAIRequester extends AbstractFixedFormatAIRequester<Boolean> {

    private static final FixedBooleanAIRequester REQUESTER = new FixedBooleanAIRequester();

    public static FixedBooleanAIRequester getInstance() {
        return REQUESTER;
    }

    public FixedBooleanAIRequester() {
        super();
    }

    public FixedBooleanAIRequester(LLMClient client) {
        super(client);
    }

    @Override
    public IResultParser<Boolean> getParser() {
        return new IResultParser<Boolean>() {
            @Override
            public Boolean parseExpectedResult(String conversationId, String result) {
                return "是".equals(getTextInTags(result));
            }
        };
    }
}
