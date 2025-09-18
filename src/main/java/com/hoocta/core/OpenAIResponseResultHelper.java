package com.hoocta.core;

import com.hoocta.utils.JsonUtils;
import com.hoocta.utils.LogUtils;

public class OpenAIResponseResultHelper {

	/**
	 * 获取响应结果中完全成功的消息。不支持其他 finish reason 的处理——直接抛异常，这次解析失败，等待人工介入。
	 * @param result
	 * @return
	 * @throws ServiceException
	 */
	public static String getCompleteStopResult(OpenAIResponseSuccessResult result) {
		if (result.getChoices() == null || result.getChoices().size() < 1) {
			LogUtils.sysout("==OpenAIResponseSuccessResult@getCompleteStopResult== " + JsonUtils.toJson(result));
			return JsonUtils.toJson(result); // 直接返回原来的内容供排查
		}
		Choice choice = result.getChoices().get(0);
		if (!choice.isCompleteMsg()) {
			// 其他的 finish reason 都不支持
			LogUtils.log("Response(openai, gpt) finish reason is not [STOP]! " + result.toString());
//			throw new ServiceException(BizCode.SERVER_BUSY);
			return null;
		}
		return choice.getMessage().getContent();
	}
}
