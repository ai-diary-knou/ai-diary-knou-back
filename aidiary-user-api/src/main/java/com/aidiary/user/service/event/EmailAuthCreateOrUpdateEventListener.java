package com.aidiary.user.service.event;

import com.aidiary.infrastructure.transport.GoogleMailSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmailAuthCreateOrUpdateEventListener {

    private final GoogleMailSender googleMailSender;

    @Async
    @EventListener
    public void handleUserPasswordUpdateEvent(EmailAuthCreateOrUpdateEvent event) {

        switch (event.getEmailSendType()) {
            case REGISTER ->  sendEmailAuthCodeToRegister(event);
            case PASSWORD_MODIFICATION ->  sendEmailAuthCodeToUpdatePassword(event);
        }

    }

    private void sendEmailAuthCodeToRegister(EmailAuthCreateOrUpdateEvent event) {
        String title = String.format("[AiDiary] 인증 코드를 확인하고 %s을 완료하세요.", "회원가입");
        String description = "아래의 인증 코드를 사용하여 이메일 인증을 완료해 주세요.";
        String content = googleMailSender.generateEmailVerificationTemplate(title, description, event.getCode());

        googleMailSender.sendMail(event.getEmail(), title, content, true);
    }

    private void sendEmailAuthCodeToUpdatePassword(EmailAuthCreateOrUpdateEvent event) {
        String title = String.format("[AiDiary] 인증 코드를 확인하고 %s을 완료하세요.", "비밀번호 변경");
        String description = "아래의 인증 코드를 사용하여 이메일 인증을 완료해 주세요.";
        String content = googleMailSender.generateEmailVerificationTemplate(title, description, event.getCode());

        googleMailSender.sendMail(event.getEmail(), title, content, true);
    }

}
