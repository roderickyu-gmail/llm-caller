package com.hoocta.core.common;

/**
 * 可返回空列表的 Requester
 * @author roderickyu Apr 28, 2025
 */
public class FixedStrListCanEmptyAIRequester extends FixedStrListAIRequester {
	
	public static final FixedStrListCanEmptyAIRequester INSTANCE = new FixedStrListCanEmptyAIRequester();
	

	@Override
	public boolean isListCanBeEmpty() {
		return true;
	}
	

}
