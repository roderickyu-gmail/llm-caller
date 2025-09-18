package com.hoocta.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class WxSignatureUtils {

	
	/**
	 * 验证请求是否来自微信
	 */
	public static boolean validateSig(String signature, String... params) {
		// 1. 将参数进行字典序排序
		Arrays.sort(params);
		for (String str : params) {
			System.out.println(str);
		}
		// 2. 将参数字符串拼接成一个字符串
		StringBuilder concatenated = new StringBuilder();
		for (String param : params) {
			concatenated.append(param);
		}

		// 3. 进行 sha1 加密
		String sha1Hash = sha1(concatenated.toString());

		// 4. 获得加密后的字符串与 signature 对比
		return sha1Hash != null && sha1Hash.equals(signature);
	}

	/**
	 * 对字符串进行 SHA-1 加密
	 * 
	 * @param input 需要加密的字符串
	 * @return 加密后的字符串
	 */
	private static String sha1(String input) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			byte[] bytes = md.digest(input.getBytes());
			StringBuilder hexString = new StringBuilder();
			for (byte b : bytes) {
				String hex = Integer.toHexString(0xff & b);
				if (hex.length() == 1) {
					hexString.append('0');
				}
				hexString.append(hex);
			}
			return hexString.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
	}
}
