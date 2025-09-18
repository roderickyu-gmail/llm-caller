package com.hoocta.core;

public class Message {
	
	private String role; // 默认
	private String content;
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
	// TODO 加入奇偶数、token、字符限制判断
	public static Message buildMessage(LLMRole role, String content) {
		Message msg = new Message();
		msg.setRole(role.getName());
		msg.setContent(content);
		return msg;
	}
	

}
