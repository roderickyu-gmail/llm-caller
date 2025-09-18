package com.hoocta.core.common;

import java.util.List;

/**
 * 返回非空的 list<String> 列表
 * @author roderickyu Feb 5, 2025
 */
public class FixedStrListAIRequester extends AbstractFixedStrListAIRequester {
	
	private static final FixedStrListAIRequester REQUESTER = new FixedStrListAIRequester();
	public static FixedStrListAIRequester getInstance() {
		return REQUESTER;
	}
	@Override
	public List<String> complete(String systemPrompt, String userPrompt, String elementSampleName,
			boolean swithToMostSmartAI) {
		return super.complete(systemPrompt, userPrompt, elementSampleName, swithToMostSmartAI);
	}


	@Override
	public List<String> complete(String systemPrompt, String userPrompt) {
		throw new UnsupportedOperationException();
	}


	@Override
	public List<String> complete(String systemPrompt, String userPrompt, boolean swithToMostSmartAI) {
		throw new UnsupportedOperationException();
	}


	@Override
	public boolean isListCanBeEmpty() {
		// 不允许为空
		return false;
	}

	public static void main(String[] args) {
		
	}
}
