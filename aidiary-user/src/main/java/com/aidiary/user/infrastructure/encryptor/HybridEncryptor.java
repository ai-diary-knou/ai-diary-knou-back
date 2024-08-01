package com.aidiary.user.infrastructure.encryptor;

import com.aidiary.common.utils.HybridEncUtil;
import com.aidiary.common.utils.HybridEncUtil.EncryptedDataBucket;
import com.aidiary.common.utils.RSAUtil;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

@Component
public class HybridEncryptor {

    @Value("${spring.encryptor.seed-key}")
    private String encryptSeedKey;
    private HybridEncUtil hybridEncUtil;
    private PublicKey publicKey;
    private PrivateKey privateKey;
    private String publicKeyString;
    private String privateKeyString;

    @PostConstruct
    private void init() throws NoSuchAlgorithmException {
        KeyPair keyPair = RSAUtil.buildKeyPairFromSeed(encryptSeedKey);
        publicKey = keyPair.getPublic();
        privateKey = keyPair.getPrivate();
        publicKeyString = RSAUtil.publicKeyToString(publicKey);
        privateKeyString = RSAUtil.privateKeyToString(privateKey);
        hybridEncUtil = new HybridEncUtil(encryptSeedKey);
    }

    public String encrypt(String plaintext) throws Exception {
        EncryptedDataBucket encryptedDataBucket = hybridEncUtil.encrypt(plaintext, publicKeyString);
        return encryptedDataBucket.rsaPublicKey() + ":" + encryptedDataBucket.aesEncryptedText();
    }

    public String decrypt(String encryptedText) throws Exception {
        String[] splits = encryptedText.split(":");
        EncryptedDataBucket encryptedDataBucket = EncryptedDataBucket.builder()
                .rsaPublicKey(splits[0])
                .aesEncryptedText(splits[1])
                .build();
        return hybridEncUtil.decrypt(encryptedDataBucket, privateKeyString);
    }

}
