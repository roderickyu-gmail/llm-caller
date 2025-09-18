package com.hoocta.core.common;

import com.hoocta.core.LLMClient;

/**
 * 请求 AI 返回普通字符串的响应（对格式没有要求）
 * @author roderickyu Jan 5, 2025
 */
public class FixedPlainTextAIRequester extends AbstractFixedFormatAIRequester<String> {

    private static final FixedPlainTextAIRequester REQUESTER = new FixedPlainTextAIRequester();

    public static FixedPlainTextAIRequester getInstance() {
        return REQUESTER;
    }

    public FixedPlainTextAIRequester() {
        super();
    }

    public FixedPlainTextAIRequester(LLMClient client) {
        super(client);
    }

    @Override
    public IResultParser<String> getParser() {
        return IResultParser.STR_PARSER;
    }
}
