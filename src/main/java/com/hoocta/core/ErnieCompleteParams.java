package com.hoocta.core;

import java.util.List;

import com.google.gson.annotations.SerializedName;

/**
 * 更多参数列表详见：https://cloud.baidu.com/doc/WENXINWORKSHOP/s/clntwmv7t
 * @author roderickyu Mar 18, 2024
 */
public class ErnieCompleteParams {
	@SerializedName("messages")
	private List<Message> messages;
	@SerializedName("max_output_tokens")
	private int maxOutputTokens;
	@SerializedName("temperature")
	private float temperature = 0.01f;
	@SerializedName("top_p")
	private float topP = 0.01f;
	@SerializedName("penalty_score")
	private float penaltyScore = 1.0f;
	@SerializedName("stream")
	private boolean stream = false;
	@SerializedName("system")
	/**
	 * 模型人设，主要用于人设设定，例如，你是xxx公司制作的AI助手，说明：
	 * （1）长度限制，最后一个message的content长度（即此轮对话的问题）和system字段总内容不能超过20000个字符，且不能超过5120 tokens
	 */
	private String system;
	
	public List<Message> getMessages() {
		return messages;
	}
	public void setMessages(List<Message> messages) {
		this.messages = messages;
	}
	public float getTemperature() {
		return temperature;
	}
	public void setTemperature(float temperature) {
		this.temperature = temperature;
	}
	
	public boolean isStream() {
		return stream;
	}
	public void setStream(boolean stream) {
		this.stream = stream;
	}
	public String getSystem() {
		return system;
	}
	public void setSystem(String system) {
		this.system = system;
	}
	public int getMaxOutputTokens() {
		return maxOutputTokens;
	}
	public void setMaxOutputTokens(int maxOutputTokens) {
		this.maxOutputTokens = maxOutputTokens;
	}
	public float getTopP() {
		return topP;
	}
	public void setTopP(float topP) {
		this.topP = topP;
	}
	public float getPenaltyScore() {
		return penaltyScore;
	}
	public void setPenaltyScore(float penaltyScore) {
		this.penaltyScore = penaltyScore;
	}
	
	
}
