package com.hoocta.utils;

import com.hoocta.core.common.FixedPlainTextAIRequester;

public class JsonRepair {
	private static FixedPlainTextAIRequester PR = FixedPlainTextAIRequester.getInstance();
	
	public static String repairJsonArray(String jsonStr) {
		StringBuilder userPrompt = new StringBuilder();
		userPrompt.append("这段JSON字符串格式可能有误，导致我用 Java 解析库解析失败。请理解这段内容，并修复这段字符串错误的符号或格式，以使之成为正确的JSON Array格式字符串。直接返回结果即可。");
		userPrompt.append("<待修复的字符串>");
		userPrompt.append(jsonStr);
		userPrompt.append("</待修复的字符串>");
		
		return PR.complete(null, userPrompt.toString());
	}
	
	public static String repairJson(String jsonStr) {
		StringBuilder userPrompt = new StringBuilder();
		userPrompt.append("这段JSON字符串格式可能有误，导致程序对这段 JSON 的解析失败。请理解这段内容和结构，并修复这段字符串错误的符号或格式，直接返回结果即可。但注意不要重构原来的数据结构，原来是什么结构就是什么结构，在原来的基础上进行修复，使之正确。");
		userPrompt.append("<待修复的字符串>");
		userPrompt.append(jsonStr);
		userPrompt.append("</待修复的字符串>");
		return PR.complete(null, userPrompt.toString(), false, true);
	}
	
	public static void main(String[] args) {
//		String jsonStr = "[\"主导跨部门协作制定微服务化架构演进路线，通过模块解耦与分布式设计提升系统扩展性及容灾能力\", \"设计并推动实施统一技术中台方案，制定标准化接口规范与服务治理策略以支撑多业务线高效复用\", \"规划混合云技术架构整合方案，设计跨平台资源调度机制与弹性扩容策略实现基础设施优化\"";
		String jsonStr = "\"[建立三级通知跟进机制：①群公告@全员 +关键信息标红；②私聊未回复同学；③线下课间二次提醒，确保重要通知24小时内响应率达100%。教学通知落实率从60%提升至98%，辅导员评价“信息枢纽零失误”]";
		System.out.println(repairJson(jsonStr));
		System.exit(0);
	}

}
