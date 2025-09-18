package com.hoocta.utils;

import com.hoocta.core.ServiceException;
import com.hoocta.llm.constants.BizCode;
import com.hoocta.llm.constants.EncryptSecrets;

/**
 * 对加密（AES）后的二进制数据进行编码（Base62），并保证编码后的字符串仅包含字母和数字。
 * @author roderickyu Jul 11, 2024
 */
public class IdEncrypter {

	public static String encryt(long id) throws ServiceException {
		try {
			String aesEncryptedId = NewAESUtils.encrypt(String.valueOf(id), EncryptSecrets.SERVER_ENCRYPT_KEY, EncryptSecrets.SERVER_ENCRYPT_IV);
			String base62EncryptedId = Base62Utils.encode(aesEncryptedId.getBytes());
			return base62EncryptedId;
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(BizCode.SERVER_BUSY);
		}
	}
	
	public static long decrypt(String encryptedId) throws ServiceException {
		byte[] debase62EncryptedIdBytes = Base62Utils.decode(encryptedId);
		try {
			String idStr = NewAESUtils.decrypt(new String(debase62EncryptedIdBytes), EncryptSecrets.SERVER_ENCRYPT_KEY, EncryptSecrets.SERVER_ENCRYPT_IV);
			return Long.parseLong(idStr);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(BizCode.SERVER_BUSY);
		}
	}
	
	public static void main(String[] args) throws Exception {
//		System.out.println(decrypt("yQanuX3NpL5wekWczaDCNPnIrmM7JDQb"));
		long cvId = 4;
		System.out.println(encryt(cvId));
		System.out.println(decrypt("wUM7F9bmdur1JTFZTKISlX4fuoOT0QAP"));
//		for (int i = 0; i < 10000; i++) {
////			long userId = new Random().nextLong(100000000000000999l);
//			long userId = 1;
//			String encryptedId = encryt(userId);
//			System.out.println(encryptedId + ", " + decrypt(encryptedId));
//		}
//		String encryptedUserId = AESUtils.encrypt(String.valueOf(userId), EncryptSecrets.SECRET_USER_ID);
//		System.out.println("encryptedUserId: " + encryptedUserId);
//		String base62EncryptedUserId = Base62Utils.encode(encryptedUserId.getBytes());
//		System.out.println("base62EncryptedUserId: " + base62EncryptedUserId);
//		
//		byte[] debase62EncryptedUserId = Base62Utils.decode(base62EncryptedUserId);
//		String userIdStr = AESUtils.decrypt(new String(debase62EncryptedUserId), EncryptSecrets.SECRET_USER_ID);
//		System.out.println("userIdStr: " + userIdStr);
		System.out.println(encryt(3));
	}
}
