package com.aidiary.user.infrastructure.encryptor;

import com.aidiary.common.utils.HybridEncUtil;
import com.aidiary.common.utils.HybridEncUtil.EncryptedDataBucket;
import com.aidiary.common.utils.RSAUtil;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

@Component
@Slf4j
@RequiredArgsConstructor
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
        try {
            EncryptedDataBucket encryptedDataBucket = hybridEncUtil.encrypt(plaintext, publicKeyString);
            log.info("private key string :: {}", privateKeyString);
            return encryptedDataBucket.rsaPublicKey() + ":" + encryptedDataBucket.aesEncryptedText();
        } catch (Exception e) {
            log.error("Encryption Failed ::", e);
            throw new Exception("Encryption failed", e);
        }
    }

    public String decrypt(String encryptedText) throws Exception {
        try {
            String[] splits = encryptedText.split(":");
            EncryptedDataBucket encryptedDataBucket = EncryptedDataBucket.builder()
                    .rsaPublicKey(splits[0])
                    .aesEncryptedText(splits[1])
                    .build();
            log.info("private key string :: {}", privateKeyString);
            return hybridEncUtil.decrypt(encryptedDataBucket, privateKeyString);
        } catch (Exception e) {
            log.error("Decryption failed ::", e);
            throw new Exception("Decryption failed", e);
        }
    }

}
