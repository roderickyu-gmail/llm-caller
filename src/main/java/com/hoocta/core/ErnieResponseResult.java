package com.hoocta.core;

import com.hoocta.utils.JsonUtils;

public class ErnieResponseResult {
	
	/**
	 * 文心一言的 API 接口有成功、失败两种情况。
	 * 
	 * 接口失败，返回：
	 * {
	 *   "error_code": 110,
	 *   "error_msg": "Access token invalid or no longer valid"
	 * }
	 * 
	 * 接口成功，则返回正常信息，见：https://cloud.baidu.com/doc/WENXINWORKSHOP/s/clntwmv7t
	 * @param json
	 * @return
	 */
	public static ErnieResponseResult buildResult(String json) {
		ErnieErrorResult errorResult = ErnieErrorResult.buildErrorResult(json);
		if (errorResult != null) {
			return errorResult;
		}
		return JsonUtils.fromJson(json, ErnieResponseSuccessResult.class);
	}
	
	

}
