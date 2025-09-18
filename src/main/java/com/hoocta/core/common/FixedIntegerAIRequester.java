package com.hoocta.core.common;

import java.util.List;

import com.hoocta.core.LLMClient;
import com.hoocta.utils.CollectionUtils;
import com.hoocta.utils.NumberParser;

/**
 * 固定返回 Integer 数值的 AI 请求器
 * @author roderickyu Jan 2, 2025
 */
public class FixedIntegerAIRequester extends AbstractFixedFormatAIRequester<Integer> {

    public static final FixedIntegerAIRequester INSTANCE = new FixedIntegerAIRequester();

    public FixedIntegerAIRequester() {
        super();
    }

    public FixedIntegerAIRequester(LLMClient client) {
        super(client);
    }

    @Override
    public IResultParser<Integer> getParser() {
        return new IResultParser<Integer>() {
            @Override
            public Integer parseExpectedResult(String conversationId, String result) {
                String resultStr = getTextInTags(result);
                if (resultStr == null) {
                    return null;
                }
                List<Integer> numbers = NumberParser.parseSequenceNumbers(resultStr);
                if (CollectionUtils.isEmpty(numbers)) {
                    return null;
                }
                return numbers.getFirst();
            }
        };
    }
}
