package com.aidiary.common.utils;

import org.junit.jupiter.api.Test;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

class RSAUtilTest {

    @Test
    public void RSA_암호화_테스트() throws Exception{

        String seed = "aidiarynewclearbumb";

        // RSA 키 쌍 생성
        KeyPair keyPair = RSAUtil.buildKeyPairFromSeed(seed);
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();

        // 공개키와 비밀키를 문자열로 변환
        String publicKeyString = RSAUtil.publicKeyToString(publicKey);
        String privateKeyString = RSAUtil.privateKeyToString(privateKey);

        System.out.println("Public Key: " + publicKeyString);
        System.out.println("Private Key: " + privateKeyString);

        // 암호화 및 복호화 테스트
        String data = "Hello, RSA!";
        String encryptedData = RSAUtil.rsaEncrypt(data, publicKey);
        String decryptedData = RSAUtil.rsaDecrypt(encryptedData, privateKey);

        System.out.println("Encrypted Data: " + encryptedData);
        System.out.println("Decrypted Data: " + decryptedData);

    }

}