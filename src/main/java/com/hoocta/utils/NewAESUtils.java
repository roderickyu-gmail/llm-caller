package com.hoocta.utils;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import com.hoocta.llm.constants.EncryptSecrets;

import javax.crypto.spec.IvParameterSpec;
import java.util.Base64;
import java.security.SecureRandom;

public class NewAESUtils {
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";
    
    // Generate a new AES key
    public static String generateKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
        keyGen.init(128); // for example, 128 bits
        SecretKey secretKey = keyGen.generateKey();
        return Base64.getEncoder().encodeToString(secretKey.getEncoded());
    }

    // Generate a new IV
    public static String generateIv() {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        return Base64.getEncoder().encodeToString(iv);
    }

    // Encrypt a string
    public static String encrypt(String data, String base64Key, String base64Iv) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(Base64.getDecoder().decode(base64Key), ALGORITHM);
        IvParameterSpec ivSpec = new IvParameterSpec(Base64.getDecoder().decode(base64Iv));
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);
        byte[] encryptedBytes = cipher.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    // Decrypt a string
    public static String decrypt(String encryptedData, String base64Key, String base64Iv) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(Base64.getDecoder().decode(base64Key), ALGORITHM);
        IvParameterSpec ivSpec = new IvParameterSpec(Base64.getDecoder().decode(base64Iv));
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
        return new String(decryptedBytes);
    }

    public static void main(String[] args) throws Exception {
    	// Jk4mnAR4KtGapfCtpk8mABGml7sCVe1cN1QbqPNAll5VTIeNf9gWm74b1bQCDA3oIWjfQpnskQOFeFNKVlzNaH9SBttwIecbkxXxjG3pxyHcL9QkdcWxNcy5yIUjGAYq4RzTMcqSzqo1nUVlbDdq+4oEdEQ3ZYiw9iuWz0c9YZs=
    	
    	String key = generateKey();
    	String iv = generateIv();
    	System.out.println(key);
    	System.out.println(iv);
//    	String encryptedData = encrypt("hello", key, iv);
//    	System.out.println("encryptedData: " + encryptedData);
//    	System.out.println("decryptedData: " + decrypt(encryptedData, key, iv));
    	// Ujat7ZY+YZeMJzL/H/LklQ==
    	// Ujat7ZY+YZeMJzL/H/LklQ==

//    	System.out.println(encrypt("hello", REQUEST_KEY, REQUEST_IV));
    	
    	System.out.println(NewAESUtils.decrypt("tSuoCivLaRfMb3a8ZrJ/Mw==", EncryptSecrets.RESPONSE_KEY, EncryptSecrets.RESPONSE_IV));
    	
    }
}
