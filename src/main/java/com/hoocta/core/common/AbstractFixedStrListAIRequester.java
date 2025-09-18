package com.hoocta.core.common;

import java.util.List;

import com.hoocta.llm.constants.AITags;
import com.hoocta.utils.CollectionUtils;
import com.hoocta.utils.JsonRepair;
import com.hoocta.utils.JsonToleranceUtils;
import com.hoocta.utils.LogUtils;
import com.hoocta.utils.StringUtils;

/**
 * 抽象的 AI 请求器，返回固定的 list<String> 格式数据
 * @author roderickyu Feb 5, 2025
 */
public abstract class AbstractFixedStrListAIRequester extends AbstractFixedFormatAIRequester<List<String>> {
	
	@Override
	public IResultParser<List<String>> getParser() {
		IResultParser<List<String>> parser = new IResultParser<List<String>>() {
			@SuppressWarnings("unchecked")
			@Override
			public List<String> parseExpectedResult(String conversationId, String result) {
				List<String> list = null;
				try {
					String textWithoutTag = getTextInTags(result); // 正常不会返回空。即便列表为空，也应该是<xmbdata>[]</xmbdata>
					if (StringUtils.isBlank(textWithoutTag)) {
						LogUtils.syserr("Reponse Null or invalid XMBDATA. Response content: " + textWithoutTag + ". Conversation id: " + conversationId);
						// 下面允许返回空 list，但数据格式必须正确！（在 xmbdata 中）
						return null;
					}
					String expectedJsonResult = ensureCleanSqureText(textWithoutTag);
					if (StringUtils.isBlank(expectedJsonResult)) {
						// 需要修复的问题：缺少[]符号。
						expectedJsonResult = JsonRepair.repairJsonArray(textWithoutTag);
					}
					list = JsonToleranceUtils.parseJson(expectedJsonResult, List.class, conversationId);
					if (!isListCanBeEmpty() && CollectionUtils.isEmpty(list)) {
						// 返回一个 null，让上层重试处理
						LogUtils.syserr("An empty list parsed, result is: " + result + ". Conversation id: " + conversationId);
						return null;
					}
				} catch (Exception e) {
					e.printStackTrace();
					LogUtils.logException("", e);
				}
				return list;
			}
		};
		return parser;
	}
	
	
	public List<String> complete(String systemPrompt, String userPrompt, String elementSampleName, boolean swithToMostSmartAI) {
		return complete(systemPrompt, userPrompt, elementSampleName, swithToMostSmartAI, true);
	}
	public List<String> complete(String systemPrompt, String userPrompt, String elementSampleName, boolean swithToMostSmartAI, boolean stream) {
		StringBuilder userPromptSb = new StringBuilder();
		if (userPrompt != null) {
			userPromptSb.append(userPrompt);
			userPrompt = "";
		}
		//TODO: 每个类型的 Requester 都定义一个输出示例：成功、失败、空等。
		userPromptSb.append("。输出结果示例：\r\n");
		userPromptSb.append("（1）若结果为空，则结果为：" + AITags.BEGIN_TAG + "[]" + AITags.END_TAG +"。\r\n");
		userPromptSb.append("（2）若结果不为空，则结果为：" + AITags.BEGIN_TAG + "[\"" + String.format("%s1", elementSampleName) + "\", \"" + String.format("%s2", elementSampleName) + "\"]" + AITags.END_TAG +"。\r\n");
		return super.complete(systemPrompt, userPromptSb.toString(), swithToMostSmartAI, stream);
	}

	/**
	 * 是否允许 list 为空（null、list.size() == 0）
	 * @return
	 */
	public abstract boolean isListCanBeEmpty();

	public static void main(String[] args) {

		FixedStrListAIRequester SR = FixedStrListAIRequester.getInstance();
		List<String> list = SR.complete("", "列举 5 种水果", "水果名", false);
		CollectionUtils.print(list);
		System.exit(0);
	
	}
}
