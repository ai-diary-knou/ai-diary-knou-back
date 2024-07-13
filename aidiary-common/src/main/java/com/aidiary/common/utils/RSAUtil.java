package com.aidiary.common.utils;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class RSAUtil {

    private static final String ALGORITHM = "RSA/ECB/PKCS1Padding";
    private static final int KEY_SIZE = 2048;

    // RSA 키 쌍 생성
    public static KeyPair buildKeyPairFromSeed(String seed) throws NoSuchAlgorithmException {
        byte[] seedBytes = seed.getBytes(StandardCharsets.UTF_8);
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(KEY_SIZE, new SecureRandom(seedBytes));
        return keyPairGenerator.generateKeyPair();
    }

    // 공개키를 문자열로 변환
    public static String publicKeyToString(PublicKey publicKey) {
        return Base64.getEncoder().encodeToString(publicKey.getEncoded());
    }

    // 비밀키를 문자열로 변환
    public static String privateKeyToString(PrivateKey privateKey) {
        return Base64.getEncoder().encodeToString(privateKey.getEncoded());
    }

    // RSA로 암호화
    public static String rsaEncrypt(String plainText, PublicKey publicKey)
            throws NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    // RSA로 복호화
    public static String rsaDecrypt(String encryptedText, PrivateKey privateKey)
            throws NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedText));
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }

    // 공개키 생성
    public static PublicKey createPublicKey(String publicKey) throws NoSuchAlgorithmException, InvalidKeySpecException {

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        byte[] binaryPublicKey = Base64.getDecoder().decode(publicKey.getBytes());
        return keyFactory.generatePublic(new X509EncodedKeySpec(binaryPublicKey));

    }

    // 비밀키 생성
    public static PrivateKey createPrivateKey(String privateKey) throws NoSuchAlgorithmException, InvalidKeySpecException {

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        byte[] binaryPrivateKey = Base64.getDecoder().decode(privateKey);
        return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(binaryPrivateKey));

    }

}
