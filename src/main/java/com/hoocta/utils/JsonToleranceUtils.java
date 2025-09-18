package com.hoocta.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;

/**
 * 具有一定容错性的 Json 解析类，可以兼容一些 Json 数据中的格式小问题（AI 有时返回的数据格式不完全正确）
 * 
 * @author roderickyu Aug 5, 2024
 */
public class JsonToleranceUtils {

	private static final ObjectMapper OBJ_MAPPER;

	static {
		 OBJ_MAPPER = JsonMapper.builder()
	                .enable(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS)
	                .enable(JsonReadFeature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER)
	                .enable(JsonReadFeature.ALLOW_UNQUOTED_FIELD_NAMES)
	                .enable(JsonReadFeature.ALLOW_SINGLE_QUOTES)
	                .enable(JsonReadFeature.ALLOW_JAVA_COMMENTS)
	                .build();
	}

	private JsonToleranceUtils() {
		// 私有构造函数以防止实例化
	}
	public static <T> T parseJson(String json, Class<T> clazz, String conversationId) {
		for (int i = 0; i < 5; i++) {
			try {
				return OBJ_MAPPER.readValue(json, clazz);
			} catch (Exception e) {
				e.printStackTrace();
				LogUtils.sysout("Exception of conversation(" + conversationId + "). Json: " + json + ". Exception: " + e.getMessage());
				json = JsonRepair.repairJson(json);
			}
		}
		return null;
	}
	public static String toJson(Object obj) {
		try {
			return OBJ_MAPPER.writeValueAsString(obj);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	public static void main(String[] args) {
		
	}
}
