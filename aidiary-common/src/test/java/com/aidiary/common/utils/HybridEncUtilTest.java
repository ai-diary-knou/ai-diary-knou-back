package com.aidiary.common.utils;

import org.junit.jupiter.api.Test;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import static org.junit.jupiter.api.Assertions.assertEquals;

class HybridEncUtilTest {

    @Test
    public void 하이브리드_암호화_테스트() throws Exception {

        // RSA 공개키와 비밀키 생성
        String seed = "aidiarynewclearbumb";
        KeyPair keyPair = RSAUtil.buildKeyPairFromSeed(seed);
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();
        String publicKeyString = RSAUtil.publicKeyToString(publicKey);
        String privateKeyString = RSAUtil.privateKeyToString(privateKey);

        HybridEncUtil hybridEncUtil = new HybridEncUtil(seed);

        // 원본 평문 설정
        String plaintext = "오늘은 특별히 여유로운 하루였다. 아침에는 일찍 일어나서 창밖으로 내다보이는 일출을 구경했다. 하늘은 맑고 바람은 부드럽게 불어와 마음이 맑아졌다. 아침 식사 후에는 오랜만에 친구들과 온라인으로 만나서 이야기를 나눴다. 서로 최근에 있었던 일들과 계획 등을 나누며 시간 가는 줄 몰랐다. 그래서 그들과 만나는 시간이 너무나도 소중했다. 오후에는 새로운 취미를 찾아보기로 했다. 오랜만에 그림을 그려보기로 마음 먹고, 동네 예술용품 가게를 방문해서 필요한 도구들을 샀다. 집에 돌아와서는 창밖의 풍경을 그려보았는데, 그 과정에서의 새로운 발견과 창조적인 즐거움이 정말 기분 좋았다. 저녁에는 가족과 함께 저녁 식사를 하면서 하루를 마무리했다. 가족들과 함께 시간을 보내는 것이 얼마나 소중하고 감사한 일인지 다시 한번 느낄 수 있었다. 하루가 저물어가면서, 내일의 일정과 할 일들을 생각하며 마음을 다잡았다. 오늘 하루의 평온함과 만족감을 잊지 않고, 내일 또 다른 새로운 하루를 기대하며 잠에 들었다.";

        // 1. 하이브리드 암호화
        HybridEncUtil.EncryptedDataBucket encryptedDataBucket = hybridEncUtil.encrypt(plaintext, publicKeyString);
        System.out.println("encryptedText : " + encryptedDataBucket.aesEncryptedText());
        System.out.println("length : " + encryptedDataBucket.aesEncryptedText().length());

        // 2. 하이브리드 복호화
        String decryptedText = hybridEncUtil.decrypt(encryptedDataBucket, privateKeyString);
        System.out.println("decryptedText : " + decryptedText);
        System.out.println("length : " + decryptedText.length());

        // ==================================
        plaintext = "오예!!!!";

        encryptedDataBucket = hybridEncUtil.encrypt(plaintext, publicKeyString);
        System.out.println("encryptedText : " + encryptedDataBucket.aesEncryptedText());
        System.out.println("length : " + encryptedDataBucket.aesEncryptedText().length());

        // 2. 하이브리드 복호화
        decryptedText = hybridEncUtil.decrypt(encryptedDataBucket, privateKeyString);
        System.out.println("decryptedText : " + decryptedText);
        System.out.println("length : " + decryptedText.length());

        // 3. 평문과 복호화된 평문이 같은지 확인
        assertEquals(plaintext, decryptedText, "Decrypted text should match the original plaintext");

        // ==================애플리케이션 재부팅 후 재현 : seed 같으면 iv 초기값 동일 ========================
        hybridEncUtil = new HybridEncUtil(seed);

        plaintext = "오예!!!!";

        encryptedDataBucket = hybridEncUtil.encrypt(plaintext, publicKeyString);
        System.out.println("encryptedText : " + encryptedDataBucket.aesEncryptedText());
        System.out.println("length : " + encryptedDataBucket.aesEncryptedText().length());

        // 2. 하이브리드 복호화
        decryptedText = hybridEncUtil.decrypt(encryptedDataBucket, privateKeyString);
        System.out.println("decryptedText : " + decryptedText);
        System.out.println("length : " + decryptedText.length());

        // 3. 평문과 복호화된 평문이 같은지 확인
        assertEquals(plaintext, decryptedText, "Decrypted text should match the original plaintext");

    }

}