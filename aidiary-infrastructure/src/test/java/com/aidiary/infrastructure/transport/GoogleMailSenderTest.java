package com.aidiary.infrastructure.transport;

import jakarta.mail.MessagingException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class GoogleMailSenderTest {

    @Autowired
    private GoogleMailSender googleMailSender;

    @Test
    public void SMTP_메일_전송_테스트() {

        // given
        String toEmail = "simdev1234@gmail.com";
        String purpose = "회원가입";
        String title = String.format("[AiDiary] 인증 코드를 확인하고 %s을 완료하세요.", purpose);
        String content = "본문입니다.";

        // when & then
        googleMailSender.sendMail(toEmail, title, content, false);

    }

    @Test
    public void MIME_메일_전송_테스트() throws MessagingException {

        // given
        String toEmail = "simdev1234@gmail.com";
        String purpose = "회원가입";
        String title = String.format("[AiDiary] 인증 코드를 확인하고 %s을 완료하세요.", purpose);
        String description = "아래의 인증 코드를 사용하여 이메일 인증을 완료해 주세요.";
        String randomCode = "RANDOM!@#$123";
        String content = googleMailSender.generateEmailVerificationTemplate(title, description, randomCode);

        // when & then
        googleMailSender.sendMail(toEmail, title, content, true);

    }
}