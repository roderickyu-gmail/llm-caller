package com.hoocta.core;

import com.google.gson.annotations.SerializedName;

public class Prompt {
	
	@SerializedName("prompt")
	private String prompt;
	@SerializedName("model")
	private String model;
	public String getPrompt() {
		return prompt;
	}
	public void setPrompt(String prompt) {
		this.prompt = prompt;
	}
	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
	}
	
}
