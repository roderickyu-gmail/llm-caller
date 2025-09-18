package com.hoocta.core;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.annotations.SerializedName;
import com.hoocta.utils.JsonUtils;

public class ErnieErrorResult extends ErnieResponseResult{
	@SerializedName("error_code")
	private int errorCode;
	@SerializedName("error_msg")
	private String errorMsg;
	public int getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
	public String getErrorMsg() {
		return errorMsg;
	}
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
	
	
	@Override
	public String toString() {
		return JsonUtils.toJson(this);
	}
	public static ErnieErrorResult buildErrorResult(String json) {
		JsonElement jsonElement = JsonParser.parseString(json);
		JsonObject jsonObject = jsonElement.getAsJsonObject();
		JsonElement codeEle = jsonObject.get("error_code");
		if (codeEle == null) {
			return null;
		}
		JsonElement errorMsgEle = jsonObject.get("error_msg");
		ErnieErrorResult msg = new ErnieErrorResult();
		msg.setErrorCode(codeEle.getAsInt());
		msg.setErrorMsg(errorMsgEle == null ? null : errorMsgEle.getAsString());
		return msg;
	}
	

	public static void main(String[] args) {
		String json = "\n"
				+ "{\n"
				+ "  \"error_code\": 110,\n"
				+ "  \"error_msg\": \"Access token invalid or no longer valid\"\n"
				+ "}\n"
				+ "";
//		String json = "\n"
//				+ "{\n"
//				+ "  \"name\": 110,\n"
//				+ "  \"age\": \"Access token invalid or no longer valid\"\n"
//				+ "}\n"
//				+ "";
		System.out.println(buildErrorResult(json));
	}
}
