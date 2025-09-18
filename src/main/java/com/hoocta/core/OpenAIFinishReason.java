package com.hoocta.core;

/**
 * OpenAI 接口的 finish reason，详见：https://platform.openai.com/docs/guides/text-generation/chat-completions-api
 * 
 * Every response will include a finish_reason. The possible values for finish_reason are:
 * stop: API returned complete message, or a message terminated by one of the stop sequences provided via the stop parameter
 * length: Incomplete model output due to max_tokens parameter or token limit
 * function_call: The model decided to call a function
 * content_filter: Omitted content due to a flag from our content filters
 * null: API response still in progress or incomplete
 * 
 * @author roderickyu Mar 19, 2024
 */
public enum OpenAIFinishReason {

	STOP("stop"),
	LENGTH("length"),
	FUNCTION_CALL("function_call"),
	CONTENT_FILTER("content_filter"),
	NULL("null");
	
	private String reason;
	
	private OpenAIFinishReason(String reason) {
		this.reason = reason;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}
}
