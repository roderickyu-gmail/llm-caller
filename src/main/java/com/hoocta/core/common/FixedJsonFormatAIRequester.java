package com.hoocta.core.common;

import com.hoocta.core.LLMClient;
import com.hoocta.utils.JsonUtils;
import com.hoocta.utils.LogUtils;

public class FixedJsonFormatAIRequester<T> extends AbstractFixedFormatAIRequester<T> {
    private static final String FORMAT = "{\"company_name\":\"公司名\",\"job_title\":\"职位名\",\"start_year\":入职时的数字年份,\"end_year\":入职时的数字月份,\"end_year\"：离职时的年份（若是当前，则写-1）,\"end_month\":离职时的月份（若是当前，则写-1）}";

    private final Class<T> clazz;

    public FixedJsonFormatAIRequester(Class<T> clazz) {
        super();
        this.clazz = clazz;
    }

    public FixedJsonFormatAIRequester(Class<T> clazz, LLMClient client) {
        super(client);
        this.clazz = clazz;
    }

    private final IResultParser<T> PARSER = new IResultParser<T>() {
        @Override
        public T parseExpectedResult(String conversationId, String result) {
            LogUtils.sysout("ParsingFomatResult: " + conversationId + ", " + result);
            return JsonUtils.fromJson(result, clazz);
        }
    };

    @Override
    public IResultParser<T> getParser() {
        return PARSER;
    }

    @Override
    public String getResultPositionRequirement() {
        return "结果以如下格式返回：" + FORMAT + "。" + super.getResultPositionRequirement();
    }
}
