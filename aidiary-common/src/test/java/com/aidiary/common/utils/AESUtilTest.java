package com.aidiary.common.utils;

import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

class AESUtilTest {

    @Test
    public void AES_암호화_테스트_Seed_iv() throws Exception{

        String seed = "aidiarynewclearbumb";

        // 1. 테스트용 비밀키 생성
        SecretKey key = AESUtil.generateKey();

        // 2. 테스트용 IV 생성
        byte[] iv = AESUtil.generateIvFromSeed(seed);

        // 3. 암호화할 평문 설정
        String plaintext = "오늘은 특별히 여유로운 하루였다. 아침에는 일찍 일어나서 창밖으로 내다보이는 일출을 구경했다. 하늘은 맑고 바람은 부드럽게 불어와 마음이 맑아졌다. 아침 식사 후에는 오랜만에 친구들과 온라인으로 만나서 이야기를 나눴다. 서로 최근에 있었던 일들과 계획 등을 나누며 시간 가는 줄 몰랐다. 그래서 그들과 만나는 시간이 너무나도 소중했다. 오후에는 새로운 취미를 찾아보기로 했다. 오랜만에 그림을 그려보기로 마음 먹고, 동네 예술용품 가게를 방문해서 필요한 도구들을 샀다. 집에 돌아와서는 창밖의 풍경을 그려보았는데, 그 과정에서의 새로운 발견과 창조적인 즐거움이 정말 기분 좋았다. 저녁에는 가족과 함께 저녁 식사를 하면서 하루를 마무리했다. 가족들과 함께 시간을 보내는 것이 얼마나 소중하고 감사한 일인지 다시 한번 느낄 수 있었다. 하루가 저물어가면서, 내일의 일정과 할 일들을 생각하며 마음을 다잡았다. 오늘 하루의 평온함과 만족감을 잊지 않고, 내일 또 다른 새로운 하루를 기대하며 잠에 들었다.";

        // 4. 암호화
        String encryptedText = AESUtil.encryptToIvAndBase64String(plaintext, key, iv);

        System.out.println("encryptedText : " + encryptedText);
        System.out.println("length : " + encryptedText.length());

        // 5. 복호화
        String decryptedText = AESUtil.decryptFromIvAndBase64String(encryptedText, key);

        System.out.println("decryptedText : " + decryptedText);
        System.out.println("length : " + decryptedText.length());

        // 6. 평문과 복호화된 평문이 같은지 확인
        assertEquals(plaintext, decryptedText, "Decrypted text should match the original plaintext");

    }

    @Test
    public void AES_암호화_테스트_랜덤iv() throws Exception {

        // 1. 테스트용 비밀키 생성
        SecretKey key = AESUtil.generateKey();

        // 2. 테스트용 IV 생성
        byte[] iv = AESUtil.generateIv();

        // 3. 암호화할 평문 설정 (짧은 테스트 문자열)
        String plaintext = "오늘은 특별히 여유로운 하루였다. 아침에는 일찍 일어나서 창밖으로 내다보이는 일출을 구경했다. 하늘은 맑고 바람은 부드럽게 불어와 마음이 맑아졌다. 아침 식사 후에는 오랜만에 친구들과 온라인으로 만나서 이야기를 나눴다. 서로 최근에 있었던 일들과 계획 등을 나누며 시간 가는 줄 몰랐다. 그래서 그들과 만나는 시간이 너무나도 소중했다. 오후에는 새로운 취미를 찾아보기로 했다. 오랜만에 그림을 그려보기로 마음 먹고, 동네 예술용품 가게를 방문해서 필요한 도구들을 샀다. 집에 돌아와서는 창밖의 풍경을 그려보았는데, 그 과정에서의 새로운 발견과 창조적인 즐거움이 정말 기분 좋았다. 저녁에는 가족과 함께 저녁 식사를 하면서 하루를 마무리했다. 가족들과 함께 시간을 보내는 것이 얼마나 소중하고 감사한 일인지 다시 한번 느낄 수 있었다. 하루가 저물어가면서, 내일의 일정과 할 일들을 생각하며 마음을 다잡았다. 오늘 하루의 평온함과 만족감을 잊지 않고, 내일 또 다른 새로운 하루를 기대하며 잠에 들었다.";

        // 4. 암호화
        byte[] encryptedText = AESUtil.encrypt(plaintext, key, iv);
        System.out.println("encryptedText (base64) : " + Base64.getEncoder().encodeToString(encryptedText));
        System.out.println("length : " + encryptedText.length);

        // 5. 복호화
        String decryptedText = AESUtil.decrypt(encryptedText, key, iv);
        System.out.println("decryptedText : " + decryptedText);
        System.out.println("length : " + decryptedText.length());

        // 6. 평문과 복호화된 평문이 같은지 확인
        assertEquals(plaintext, decryptedText, "Decrypted text should match the original plaintext");
    }

}