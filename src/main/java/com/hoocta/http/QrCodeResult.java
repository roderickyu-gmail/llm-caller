package com.hoocta.http;

import com.google.gson.annotations.SerializedName;

public class QrCodeResult {
	@SerializedName("ticket")
	private String ticket;
	@SerializedName("expire_seconds")
	private String expire_seconds;
	@SerializedName("url")
	private String url;
	public String getTicket() {
		return ticket;
	}
	public void setTicket(String ticket) {
		this.ticket = ticket;
	}
	public String getExpire_seconds() {
		return expire_seconds;
	}
	public void setExpire_seconds(String expire_seconds) {
		this.expire_seconds = expire_seconds;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
}
