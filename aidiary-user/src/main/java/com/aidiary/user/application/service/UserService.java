package com.aidiary.user.application.service;

import com.aidiary.common.enums.ErrorCode;
import com.aidiary.common.enums.UserStatus;
import com.aidiary.common.exception.UserException;
import com.aidiary.common.utils.RandomCodeGenerator;
import com.aidiary.user.application.dto.UserRequestBundle.UserEmailAndAuthCode;
import com.aidiary.user.application.dto.UserRequestBundle.UserEmailAuthCodeSentRequest;
import com.aidiary.user.application.dto.UserRequestBundle.UserRegisterRequest;
import com.aidiary.user.application.dto.UserRequestBundle.UserValidateDuplicateRequest;
import com.aidiary.user.domain.entity.UserEmailAuthsEntity;
import com.aidiary.user.domain.entity.UsersEntity;
import com.aidiary.user.domain.repository.JpaUserEmailAuthsRepository;
import com.aidiary.user.domain.repository.JpaUsersRepository;
import com.aidiary.user.infrastructure.transport.GoogleMailSender;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserService {

    private final JpaUsersRepository jpaUsersRepository;
    private final JpaUserEmailAuthsRepository jpaUserEmailAuthsRepository;
    private final GoogleMailSender googleMailSender;

    public void validateUserDuplication(UserValidateDuplicateRequest request) {

        switch (request.type()) {
            case "email" -> validateEmailDuplication(request.value());
            case "nickname" -> validateNicknameDuplication(request.value());
        }

    }

    private void validateEmailDuplication(String email) {

        if (jpaUsersRepository.existsByEmail(email)) {
            throw new UserException("User already registered. Please use another email.", ErrorCode.USER_ALREADY_REGISTERED);
        }
    }

    private void validateNicknameDuplication(String nickname) {

        if (jpaUsersRepository.existsByNickname(nickname)) {
            throw new UserException("User already registered. Please use another nickname.", ErrorCode.USER_ALREADY_REGISTERED);
        }
    }

    @Transactional
    public void createRandomCodeAndSendEmail(UserEmailAuthCodeSentRequest request) throws MessagingException {

        if ("register".equals(request.type())) {
            validateEmailDuplication(request.email());
        }

        if ("password-modification".equals(request.type())) {
            validateUserNotExistByEmail(request.email());
        }

        String randomCode = RandomCodeGenerator.getInstance().createAlphanumericCodeWithSpecialKeys();

        Optional<UserEmailAuthsEntity> optionalUserEmailAuths = jpaUserEmailAuthsRepository.findByEmail(request.email());

        if (optionalUserEmailAuths.isPresent()) {
            UserEmailAuthsEntity userEmailAuthsEntity = optionalUserEmailAuths.get();
            userEmailAuthsEntity.updateCodeAndConfirmedAt(randomCode);
        } else {
            jpaUserEmailAuthsRepository.save(
                    UserEmailAuthsEntity.builder()
                            .email(request.email())
                            .code(randomCode)
                            .createdAt(LocalDateTime.now())
                            .build()
            );
        }

        String purpose = "register".equals(request.type()) ? "회원가입" : "비밀번호 변경";
        String title = String.format("[AiDiary] 인증 코드를 확인하고 %s을 완료하세요.", purpose);
        String description = "아래의 인증 코드를 사용하여 이메일 인증을 완료해 주세요.";
        String content = googleMailSender.generateEmailVerificationTemplate(title, description, randomCode);

        googleMailSender.sendMail(request.email(), title, content, true);

    }

    private void validateUserNotExistByEmail(String email) {

        if (!jpaUsersRepository.existsByEmail(email)) {
            throw new UserException(ErrorCode.USER_NOT_EXIST);
        }

    }

    @Transactional
    public void confirmAuthCodeByEmail(UserEmailAndAuthCode request) {

        UserEmailAuthsEntity userEmailAuthsEntity = jpaUserEmailAuthsRepository.findByEmail(request.email())
                .orElseThrow(() -> new UserException(ErrorCode.EMAIL_AUTH_FAIL));

        if (!userEmailAuthsEntity.getCode().equals(request.code())) {
            log.info("인증 코드가 불일치 합니다.");
            throw new UserException(ErrorCode.EMAIL_AUTH_FAIL);
        }

        LocalDateTime expireTime = userEmailAuthsEntity.getCreatedAt().plusMinutes(5);
        LocalDateTime currentTime = LocalDateTime.now();
        if (expireTime.isBefore(currentTime)) {
            throw new UserException(ErrorCode.AUTH_CODE_EXPIRED);
        }

        userEmailAuthsEntity.updateConfirmedAt(currentTime);

    }


    public void register(UserRegisterRequest request) {

        UserEmailAuthsEntity userEmailAuthsEntity = jpaUserEmailAuthsRepository.findByEmail(request.email())
                .orElseThrow(() -> new UserException(ErrorCode.EMAIL_NOT_AUTHORIZED));

        if (!request.password().equals(request.rePassword())) {
            throw new UserException("Invalid Parameter. password and rePassword mismatch.", ErrorCode.INVALID_PARAMETER);
        }

        if (jpaUsersRepository.existsByEmail(request.email())) {
            throw new UserException(ErrorCode.USER_ALREADY_REGISTERED);
        }

        jpaUsersRepository.save(
                UsersEntity.builder()
                        .email(request.email())
                        .nickname(request.nickname())
                        //.password(rsaEncryptedBase64Password)
                        .status(UserStatus.ACTIVE)
                        .loginAttemptCnt(0)
                        .build()
        );

    }
}
