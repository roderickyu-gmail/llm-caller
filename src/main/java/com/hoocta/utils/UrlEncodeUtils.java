package com.hoocta.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.hoocta.core.ServiceException;
import com.hoocta.llm.constants.BizCode;

public class UrlEncodeUtils {

	
	public static String encode(String str) throws ServiceException {
		try {
			return URLEncoder.encode(str, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			throw new ServiceException(BizCode.SERVER_BUSY);
		}
	}
}
