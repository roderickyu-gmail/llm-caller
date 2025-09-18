package com.hoocta.core;

import com.google.gson.annotations.SerializedName;

public class Usage {

	@SerializedName("completion_tokens")
	private int completionTokens;
	@SerializedName("prompt_tokens")
	private int promptTokens;
	@SerializedName("total_tokens")
	private int totalTokens;
	public int getCompletionTokens() {
		return completionTokens;
	}
	public void setCompletionTokens(int completionTokens) {
		this.completionTokens = completionTokens;
	}
	public int getPromptTokens() {
		return promptTokens;
	}
	public void setPromptTokens(int promptTokens) {
		this.promptTokens = promptTokens;
	}
	public int getTotalTokens() {
		return totalTokens;
	}
	public void setTotalTokens(int totalTokens) {
		this.totalTokens = totalTokens;
	}
	
}
