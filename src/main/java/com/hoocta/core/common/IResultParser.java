package com.hoocta.core.common;

import com.hoocta.llm.constants.AITags;

/**
 * AI 响应结果解析接口，用于解析响应结果及判断格式是否合法
 * @author roderickyu Jul 23, 2024
 */
public interface IResultParser<T> {
	
	public static final IResultParser<String> STR_PARSER = new IResultParser<String>() {
		@Override
		public String parseExpectedResult(String conversationId, String result) {
			String resultStr = getTextInTags(result);
			return resultStr;
		}
	};
	
	
	/**
	 * 是否为期望的结果格式，是的话返回具体的期望结果，否则返回 null。有时 AI 会出错，不按要求返回结果。
	 * 
	 * @param result
	 * @return
	 */
	public T parseExpectedResult(String conversationId, String result);
	
	/**
	 * 如果同时满足存 BEGIN_TAG 和 END_TAG，那么解析出来其中的内容，否则简单粗暴直接返回全部内容
	 */
	default String getTextInTags(String data) {
		if (data == null) {
			return null;
		}
		int beginIndex = data.indexOf(AITags.BEGIN_TAG);
		int lastIndex = data.indexOf(AITags.END_TAG);
		if (beginIndex >= 0 && lastIndex >= 0) {
			return data.substring(beginIndex + AITags.BEGIN_TAG.length(), lastIndex).trim();
		}
		else {
			// 返回的数据不够严谨（未使用 AITags），那么直接当成 null 处理
			return null;
		}
//		return data;
	}
	/**
	 * 尽量拿到 JSON 格式响应。有时 AI 出问题，格式不能严格按照我们的要求返回。
	 * 比如，偶尔返回「{"result":"是","reason":"xxx"}>」，多返回一个「>」。
	 * @param str
	 * @return
	 */
	default String ensureCleanBraceText(String str) {
		int firstIndex = str.indexOf("{");
		int lastIndex = str.lastIndexOf("}");
		return str.substring(firstIndex, lastIndex + 1);
	}
	/**
	 * 原因同上，见「ensureCleanBraceText」方法
	 * @param str
	 * @return
	 */
	default String ensureCleanSqureText(String str) {
		int firstIndex = str.indexOf("[");
		int lastIndex = str.lastIndexOf("]");
		return str.substring(firstIndex, lastIndex + 1);
	}
}
