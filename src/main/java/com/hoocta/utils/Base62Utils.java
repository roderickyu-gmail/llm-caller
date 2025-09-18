package com.hoocta.utils;

import java.math.BigInteger;

/**
 * Base62 编码使用 0-9、A-Z、a-z 共 62 个字符，可以确保加密后的字符串仅包含字母和数字。
 * 
 * @author roderickyu Jul 11, 2024
 */
public class Base62Utils {
	private static final String CHARACTERS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
	private static final BigInteger BASE = BigInteger.valueOf(CHARACTERS.length());

	public static String encode(byte[] data) {
		BigInteger number = new BigInteger(1, data);
		StringBuilder encoded = new StringBuilder();

		while (number.compareTo(BASE) >= 0) {
			BigInteger[] divmod = number.divideAndRemainder(BASE);
			encoded.insert(0, CHARACTERS.charAt(divmod[1].intValue()));
			number = divmod[0];
		}
		encoded.insert(0, CHARACTERS.charAt(number.intValue()));

		return encoded.toString();
	}

	public static byte[] decode(String data) {
		BigInteger number = BigInteger.ZERO;

		for (int i = 0; i < data.length(); i++) {
			number = number.multiply(BASE).add(BigInteger.valueOf(CHARACTERS.indexOf(data.charAt(i))));
		}

		return number.toByteArray();
	}
}
