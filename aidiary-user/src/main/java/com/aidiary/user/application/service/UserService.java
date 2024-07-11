package com.aidiary.user.application.service;

import com.aidiary.common.enums.ErrorCode;
import com.aidiary.common.exception.UserException;
import com.aidiary.common.utils.RandomCodeGenerator;
import com.aidiary.user.application.dto.UserRequestBundle.*;
import com.aidiary.user.domain.entity.UserEmailAuthsEntity;
import com.aidiary.user.domain.repository.JpaUserEmailAuthsRepository;
import com.aidiary.user.domain.repository.JpaUsersRepository;
import com.aidiary.user.infrastructure.transport.MailSender;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class UserService {

    private final JpaUsersRepository jpaUsersRepository;
    private final JpaUserEmailAuthsRepository jpaUserEmailAuthsRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final MailSender mailSender;

    public void validateUserDuplication(UserValidateDuplicateRequest request) {

        switch (request.type()) {
            case "email" -> validateEmailDuplication(request.value());
            case "nickname" -> validateNicknameDuplication(request.value());
        }

    }

    private void validateEmailDuplication(String email) {
        if (jpaUsersRepository.existsByEmail(email)) {
            throw new UserException(ErrorCode.USER_ALREADY_REGISTERED);
        }
    }

    private void validateNicknameDuplication(String nickname) {
        if (jpaUsersRepository.existsByNickname(nickname)) {
            throw new UserException(ErrorCode.USER_ALREADY_REGISTERED);
        }
    }

    public void createRandomCodeAndSendEmail(String email){

        String randomCode = RandomCodeGenerator.getInstance().createAlphanumericCodeWithSpecialKeys();

        jpaUserEmailAuthsRepository.save(
                UserEmailAuthsEntity.builder()
                        .email(email)
                        .code(randomCode)
                        .createdAt(LocalDateTime.now())
                        .build()
        );

        String title = "[AiDiary] 인증 코드를 확인하고 회원가입을 완료하세요.";
        String content = mailSender.generateEmailVerificationTemplate(randomCode);

        mailSender.sendMail(email, title, content);

    }

}
