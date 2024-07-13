package com.aidiary.common.utils;

import jakarta.annotation.PostConstruct;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;


public class HybridEncUtil {

    private final String seed;
    private byte[] iv;

    public HybridEncUtil(String seed) {
        this.seed = seed;
        this.iv = AESUtil.generateIvFromSeed(seed);
    }

    @Builder
    public record EncryptedDataBucket(String rsaPublicKey, String aesEncryptedText){}


    // 하이브리드 암호화 수행
    public EncryptedDataBucket encrypt(String plaintext, String publicKeyString) throws Exception {

        PublicKey publicKey = RSAUtil.createPublicKey(publicKeyString);
        SecretKey aesKey = AESUtil.generateKey();

        byte[] aesEncryptedBytes = AESUtil.encrypt(plaintext, aesKey, iv);
        String aesEncryptedText = Base64.getEncoder().encodeToString(aesEncryptedBytes);

        String rsaPublicKeyString = RSAUtil.rsaEncrypt(Base64.getEncoder().encodeToString(aesKey.getEncoded()), publicKey);

        return EncryptedDataBucket.builder()
                .rsaPublicKey(rsaPublicKeyString)
                .aesEncryptedText(aesEncryptedText)
                .build();
    }

    // 하이브리드 복호화 수행
    public String decrypt(EncryptedDataBucket encryptedDataBucket, String privateKeyString) throws Exception {

        PrivateKey privateKey = RSAUtil.createPrivateKey(privateKeyString);

        byte[] aesKeyBytesFromRsaPublicKey = Base64.getDecoder().decode(RSAUtil.rsaDecrypt(encryptedDataBucket.rsaPublicKey(), privateKey));
        SecretKey aesKey = new SecretKeySpec(aesKeyBytesFromRsaPublicKey, "AES");

        byte[] aesEncryptedText = Base64.getDecoder().decode(encryptedDataBucket.aesEncryptedText());
        String decryptedText = AESUtil.decrypt(aesEncryptedText, aesKey, iv);

        return decryptedText;
        //return new String(decryptedText.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);
    }

}
