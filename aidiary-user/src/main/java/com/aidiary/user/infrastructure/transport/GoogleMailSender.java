package com.aidiary.user.infrastructure.transport;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Slf4j
@Component
@RequiredArgsConstructor
public class GoogleMailSender {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    public void sendMail(String toEmail, String title, String content, boolean isHtml) throws MessagingException {

        if (isHtml) {
            sendHtmlMail(toEmail, title, content);
            return;
        }

        sendTextMail(toEmail, title, content);
    }

    private void sendTextMail(String toEmail, String title, String content) {

        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(toEmail);
        simpleMailMessage.setSubject(title);
        simpleMailMessage.setText(content);
        mailSender.send(simpleMailMessage);

    }

    private void sendHtmlMail(String toEmail, String title, String content) throws MessagingException {

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false, "UTF-8");

        helper.setTo(toEmail);
        helper.setSubject(title);
        helper.setText(content, true);

        mailSender.send(mimeMessage);

    }

    public String generateEmailVerificationTemplate(String code){

        String title = "[AiDiary] 인증 코드를 확인하고 회원가입을 완료하세요.";
        String description = "회원 가입을 진심으로 축하드립니다! 아래의 인증 코드를 사용하여 이메일 인증을 완료해 주세요.";

        Context context = new Context();
        context.setVariable("title", title);
        context.setVariable("description", description);
        context.setVariable("verificationCode", code);

        return templateEngine.process("mail-auth", context);
    }

}
