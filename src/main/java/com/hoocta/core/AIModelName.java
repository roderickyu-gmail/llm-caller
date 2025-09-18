package com.hoocta.core;

public enum AIModelName {
	
	GPT4("GPT_4"),
	ERNIE4("ernie-bot-4");
	
	
	
	private String name;
	private AIModelName(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
}
