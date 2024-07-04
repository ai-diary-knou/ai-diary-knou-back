package com.aidiary.user.application.service;

import com.aidiary.common.enums.ErrorCode;
import com.aidiary.common.exception.UserException;
import com.aidiary.user.domain.entity.UserEmailAuths;
import com.aidiary.user.domain.repository.UserEmailAuthsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@Service
public class MailService {

    private final JavaMailSender mailSender;
    private final UserEmailAuthsRepository emailAuthsRepository;

    public int mailSend(final String email) {
        int code = createRandomNumber();
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(email);
        simpleMailMessage.setSubject("회원가입 인증번호입니다.");
        simpleMailMessage.setText(String.valueOf(createRandomNumber()));
        mailSender.send(simpleMailMessage);

        emailAuthsRepository.save(UserEmailAuths.builder()
                .email(email)
                .code(code)
                .createdAt(LocalDateTime.now())
                .build());
        return code;
    }

    @Transactional
    public void confirmAuthCode(final String email, int code) {
        LocalDateTime now = LocalDateTime.now();
        log.info("email = {}", email);
        log.info("code = {}", code);
        UserEmailAuths userEmailAuths = emailAuthsRepository.findByEmailAndCode(email, code)
                .orElseThrow(() -> new UserException(ErrorCode.INVALID_PARAMETER));

        if (now.isAfter(userEmailAuths.getCreatedAt().plusMinutes(5))) {
            throw new UserException(ErrorCode.AUTH_CODE_EXPIRED);
        }
        userEmailAuths.updateConfirmedAt(now);
    }

    private int createRandomNumber() {
        return 100000 + (int) (Math.random() * 900000);
    }
}
