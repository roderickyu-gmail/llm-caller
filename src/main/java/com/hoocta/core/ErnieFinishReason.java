package com.hoocta.core;
/**
 * 输入内容标识。
 * 详见：https://cloud.baidu.com/doc/WENXINWORKSHOP/s/clntwmv7t
 * @author roderickyu Mar 19, 2024
 */
public enum ErnieFinishReason {

	NORMAL("normal"),// 正常是 normal
	STOP("stop"),
	LENGTH("length"),
	CONTENT_FILTER("content_filter");
	
	private String reason;
	
	private ErnieFinishReason(String reason) {
		this.reason = reason;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}
	
	
	
}
