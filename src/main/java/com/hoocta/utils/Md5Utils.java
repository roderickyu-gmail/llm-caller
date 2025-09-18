package com.hoocta.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Md5Utils {

	public static String md5WithSalt(String input, String salt) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			String saltedInput = input + salt;
			byte[] messageDigest = md.digest(saltedInput.getBytes());
			StringBuilder hexString = new StringBuilder();
			for (byte b : messageDigest) {
				String hex = Integer.toHexString(0xff & b);
				if (hex.length() == 1)
					hexString.append('0');
				hexString.append(hex);
			}
			return hexString.toString();
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}
}
