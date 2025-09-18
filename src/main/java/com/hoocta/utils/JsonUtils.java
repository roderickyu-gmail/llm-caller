package com.hoocta.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JsonUtils {

	private static final Gson GSON = new GsonBuilder().disableHtmlEscaping().create(); // 禁止 HTML 转义
	public static String toJson(Object obj) {
		return GSON.toJson(obj);
	}
	public static <T> T fromJson(String json, Class<T> T) {
		return GSON.fromJson(json, T);
	}
	
	public static void main(String[] args) {
		
	}
}
