package com.aidiary.user.application.service;

import com.aidiary.common.enums.ErrorCode;
import com.aidiary.common.exception.UserException;
import com.aidiary.common.utils.RandomCodeGenerator;
import com.aidiary.user.application.dto.UserRequestBundle.UserValidateDuplicateRequest;
import com.aidiary.user.domain.entity.UserEmailAuthsEntity;
import com.aidiary.user.domain.repository.JpaUserEmailAuthsRepository;
import com.aidiary.user.domain.repository.JpaUsersRepository;
import com.aidiary.user.domain.validator.EmailValidator;
import com.aidiary.user.domain.validator.NicknameValidator;
import com.aidiary.user.infrastructure.transport.GoogleMailSender;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserService {

    private final JpaUsersRepository jpaUsersRepository;
    private final JpaUserEmailAuthsRepository jpaUserEmailAuthsRepository;
    private final PasswordEncoder passwordEncoder;
    private final GoogleMailSender googleMailSender;

    public void validateUserDuplication(UserValidateDuplicateRequest request) {

        switch (request.type()) {
            case "email" -> validateEmailDuplication(request.value());
            case "nickname" -> validateNicknameDuplication(request.value());
        }

    }

    private void validateEmailDuplication(String email) {

        if (!EmailValidator.isValid(email)) {
            throw new UserException("Invalid Parameter. Email can only be ^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\\\.[a-zA-Z]{2,}$", ErrorCode.INVALID_PARAMETER);
        }

        if (jpaUsersRepository.existsByEmail(email)) {
            throw new UserException(ErrorCode.USER_ALREADY_REGISTERED);
        }
    }

    private void validateNicknameDuplication(String nickname) {

        if (!NicknameValidator.isValid(nickname)) {
            throw new UserException("Invalid Parameter. Nickname can only be ^([가-힣a-zA-Z0-9]*)$", ErrorCode.INVALID_PARAMETER);
        }

        if (jpaUsersRepository.existsByNickname(nickname)) {
            throw new UserException(ErrorCode.USER_ALREADY_REGISTERED);
        }
    }

    public void createRandomCodeAndSendEmail(String email) throws MessagingException {

        String randomCode = RandomCodeGenerator.getInstance().createAlphanumericCodeWithSpecialKeys();

        jpaUserEmailAuthsRepository.save(
                UserEmailAuthsEntity.builder()
                        .email(email)
                        .code(randomCode)
                        .createdAt(LocalDateTime.now())
                        .build()
        );

        String title = "[AiDiary] 인증 코드를 확인하고 회원가입을 완료하세요.";
        String content = googleMailSender.generateEmailVerificationTemplate(randomCode);

        log.info(content);

        googleMailSender.sendMail(email, title, content, true);

    }

}
