package com.aidiary.common.utils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class AESUtil {

    private static final String ALGORITHM = "AES/CBC/PKCS5Padding"; // CBC 모드와 PKCS5 패딩 사용
    private static final int AES_KEY_SIZE = 256;
    private static final int IV_BIT_SIZE = 128;

    // AES 시크릿키 생성
    public static SecretKey generateKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(AES_KEY_SIZE);
        return keyGen.generateKey();
    }

    // 무작위 IV 생성
    public static byte[] generateIv() {
        byte[] iv = new byte[IV_BIT_SIZE / 8];
        new java.security.SecureRandom().nextBytes(iv);
        return iv;
    }

    public static byte[] generateIvFromSeed(String seed) {
        byte[] seedBytes = seed.getBytes(StandardCharsets.UTF_8);
        byte[] iv = new byte[IV_BIT_SIZE / 8];
        new java.security.SecureRandom(seedBytes).nextBytes(iv);
        return iv;
    }

    // 암호화
    public static String encryptToIvAndBase64String(String plaintext, SecretKey key, byte[] iv) throws Exception {

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
        byte[] ciphertext = cipher.doFinal(plaintext.getBytes());

        String ivString = Base64.getEncoder().encodeToString(iv);
        String ciphertextString = Base64.getEncoder().encodeToString(ciphertext);
        return ivString + ":" + ciphertextString;
    }

    // 암호화
    public static byte[] encrypt(String plaintext, SecretKey key, byte[] iv) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
        return cipher.doFinal(plaintext.getBytes());
    }

    // 복호화
    public static String decryptFromIvAndBase64String(String encryptedText, SecretKey key) throws Exception {
        String[] parts = encryptedText.split(":");
        byte[] iv = Base64.getDecoder().decode(parts[0]);
        byte[] ciphertext = Base64.getDecoder().decode(parts[1]);

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);
        byte[] plaintext = cipher.doFinal(ciphertext);
        return new String(plaintext);
    }

    // 복호화
    public static String decrypt(byte[] ciphertext, SecretKey key, byte[] iv) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);
        byte[] plaintext = cipher.doFinal(ciphertext);
        return new String(plaintext);
    }

}
