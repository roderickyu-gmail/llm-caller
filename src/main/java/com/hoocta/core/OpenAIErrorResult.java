package com.hoocta.core;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hoocta.utils.JsonUtils;

public class OpenAIErrorResult extends OpenAIResponseResult{
	// {"error":{"message":"无效的令牌 (request id: 2024031818293511967485424587671)","type":"shell_api_error"}}
	private OpenAIError error;
	public OpenAIError getError() {
		return error;
	}
	public void setError(OpenAIError error) {
		this.error = error;
	}
	@Override
	public String toString() {
		return JsonUtils.toJson(this);
	}
	public static OpenAIErrorResult buildErrorResult(String json) {
		JsonElement jsonElement = JsonParser.parseString(json);
		JsonObject jsonObject = jsonElement.getAsJsonObject();
		JsonElement codeEle = jsonObject.get("error");
		if (codeEle == null) {
			return null;
		}
		return JsonUtils.fromJson(json, OpenAIErrorResult.class);
	}
}
