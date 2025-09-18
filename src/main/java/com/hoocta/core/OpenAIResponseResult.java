package com.hoocta.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.hoocta.utils.CollectionUtils;
import com.hoocta.utils.JsonUtils;
import com.hoocta.utils.StringUtils;

public class OpenAIResponseResult {
	
	/**
	 * OpenAI 响应有失败、成功两种情况。
	 * 
	 * 失败格式如：{"error":{"message":"无效的令牌 (request id: 2024031818293511967485424587671)","type":"shell_api_error"}}
	 * 成功：https://platform.openai.com/docs/guides/text-generation/chat-completions-api
	 * 
	 */

	private static OpenAIResponseResult buildResult(String json) {
		OpenAIErrorResult errorResult = OpenAIErrorResult.buildErrorResult(json);
		if (errorResult != null) {
			return errorResult;
		}
		return JsonUtils.fromJson(json, OpenAIResponseSuccessResult.class);
	}
	
	public static OpenAIResponseResult buildResult(String json, boolean stream) {
		return stream ? transferStreamResultToNonStreamResult(json) : buildResult(json);
	}
	
	
	private static final String STREAM_RESPONSE_SSE_DATA_TAG = "data: "; // SSE 协议固定的字段
	private static final String STREAM_AI_MODEL_DONE_TAG = "[DONE]"; // Deepseek 流式响应结束符号标记
	
	/**
	 * 将流式响应的结果封装成非流式响应的普通结果
	 * @param <T>
	 * @param responseContent
	 * @return
	 */
	private static OpenAIResponseResult transferStreamResultToNonStreamResult(String json) {
		@SuppressWarnings("unchecked")
		List<String> dataList = (List<String>) JsonUtils.fromJson(json, List.class);
		OpenAIResponseSuccessResult result = new OpenAIResponseSuccessResult();
		StringBuilder totolContent = new StringBuilder();
		String role = null;
		Choice choice = new Choice();
		for (String data : dataList) {
			if (StringUtils.isBlank(data)) {
				continue; // 流式响应在每个 data 数据的下一行会返回空串
			}
			String dataWithoutDataTag = data.substring(STREAM_RESPONSE_SSE_DATA_TAG.length());
			if (STREAM_AI_MODEL_DONE_TAG.equals(dataWithoutDataTag)) {
				break;
			}
			OpenAIResponseSuccessResult subResult = JsonUtils.fromJson(dataWithoutDataTag, OpenAIResponseSuccessResult.class);
			if (result.getCreated() <= 0) {
				result.setCreated(subResult.getCreated());
			}
			if (StringUtils.isBlank(result.getModel())) {
				result.setModel(subResult.getModel());
			}
			if (StringUtils.isBlank(result.getId())) {
				result.setId(subResult.getId());
			}
			if (StringUtils.isBlank(result.getSystemFingerprint())) {
				result.setSystemFingerprint(subResult.getSystemFingerprint());
			}
			if (subResult.getUsage() != null) {
				result.setUsage(subResult.getUsage());
			}
//			System.out.println("subResult: " + JsonUtils.toJson(subResult));
			if (!CollectionUtils.isEmpty(subResult.getChoices())) {
				Choice subChoice = subResult.getChoices().getFirst();
				DeltaMessage delta = subChoice.getDelta();
				String subContent = delta.getContent();
				if (role == null) {
					role = delta.getRole();
				}
				if (choice.getFinishReason() == null) {
					choice.setFinishReason(subChoice.getFinishReason());
				}
				if (StringUtils.isNotBlank(subContent) && !subContent.equalsIgnoreCase("null")) { // 会返回 "content":null
					totolContent.append(subContent);
				}
			}
		}
		Message msg = new Message();
		msg.setRole(role);
		msg.setContent(totolContent.toString());
		choice.setMessage(msg);
		List<Choice> choices = new ArrayList<>();
		choices.add(choice);
		result.setChoices(choices);
		return result;
	}
	
	public static void main(String[] args) {
		List<String> dataList = Arrays.asList(
				"data: {\"choices\":[{\"delta\":{\"content\":\"\",\"role\":\"assistant\"},\"index\":0,\"logprobs\":null,\"finish_reason\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1742555776,\"system_fingerprint\":null,\"model\":\"deepseek-v3\",\"id\":\"chatcmpl-ce3907b4-1238-9ad9-b006-8d493b4c2fb7\"}",
				"data: {\"choices\":[{\"delta\":{\"content\":\">\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1742555776,\"system_fingerprint\":null,\"model\":\"deepseek-v3\",\"id\":\"chatcmpl-ce3907b4-1238-9ad9-b006-8d493b4c2fb7\"}",
				"data: {\"choices\":[{\"finish_reason\":\"stop\",\"delta\":{\"content\":\"\"},\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1742555776,\"system_fingerprint\":null,\"model\":\"deepseek-v3\",\"id\":\"chatcmpl-ce3907b4-1238-9ad9-b006-8d493b4c2fb7\"}",
				"data: [DONE]");
		OpenAIResponseResult result = transferStreamResultToNonStreamResult(JsonUtils.toJson(dataList));
		System.out.println(result);
	}
	
}
