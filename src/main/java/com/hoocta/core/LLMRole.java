package com.hoocta.core;

public enum LLMRole {
	
	USER("user"),
	ASISTANT("assistant"),
	SYSTEM("system");// OpenAI 设定系统角色时，在 messages 中设定该值。而 Ernie 是通过请求参数设定（body.system参数）

	private String name;
	private LLMRole(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
