package com.hoocta.core;

public class OpenAIError {
//	{"error":{"message":"无效的令牌 (request id: 2024031818293511967485424587671)","type":"shell_api_error"}}
	
	private String message;
	private String type;
	
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
}
