package com.hoocta.core;

import com.google.gson.annotations.SerializedName;
import com.hoocta.utils.JsonUtils;

public class Choice {
	@SerializedName("finish_reason")
	private String finishReason; // "stop"
	private int index;
	private Message message;
	private DeltaMessage delta; // 流式响应会返回这样的数据：{"choices":[{"delta":{"content":"","role":"assistant"},"index":0,"logprobs":null,"finish_reason":null}],"object":"chat.completion.chunk","usage":null,"created":1742555776,"system_fingerprint":null,"model":"deepseek-v3","id":"chatcmpl-ce3907b4-1238-9ad9-b006-8d493b4c2fb7"}

	public String getFinishReason() {
		return finishReason;
	}
	public void setFinishReason(String finishReason) {
		this.finishReason = finishReason;
	}
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public Message getMessage() {
		return message;
	}
	public void setMessage(Message message) {
		this.message = message;
	}
	
	/**
	 * @return true, if API returned complete message, or a message terminated by one of the stop sequences provided via the stop parameter.
	 * @see https://platform.openai.com/docs/guides/text-generation/chat-completions-api
	 */
	public boolean isCompleteMsg() {
		return OpenAIFinishReason.STOP.getReason().equals(getFinishReason());
	}
	@Override
	public String toString() {
		return JsonUtils.toJson(this);
	}
	public DeltaMessage getDelta() {
		return delta;
	}
	public void setDelta(DeltaMessage delta) {
		this.delta = delta;
	}
}
