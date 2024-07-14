package com.aidiary.user.application.service;

import com.aidiary.common.enums.ErrorCode;
import com.aidiary.common.enums.UserStatus;
import com.aidiary.common.exception.UserException;
import com.aidiary.common.utils.RandomCodeGenerator;
import com.aidiary.common.utils.SHA256Util;
import com.aidiary.user.application.dto.UserRequestBundle.*;
import com.aidiary.user.application.service.security.JwtTokenProvider;
import com.aidiary.user.application.service.security.JwtTokenProvider.UserClaims;
import com.aidiary.user.domain.entity.UserEmailAuthsEntity;
import com.aidiary.user.domain.entity.UsersEntity;
import com.aidiary.user.domain.repository.JpaUserEmailAuthsRepository;
import com.aidiary.user.domain.repository.JpaUsersRepository;
import com.aidiary.user.infrastructure.transport.GoogleMailSender;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

import static com.aidiary.common.enums.UserStatus.ACTIVE;
import static com.aidiary.common.enums.UserStatus.BLOCKED;
import static org.springframework.security.core.userdetails.User.withUsername;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserService implements UserDetailsService {

    private final JpaUsersRepository jpaUsersRepository;
    private final JpaUserEmailAuthsRepository jpaUserEmailAuthsRepository;
    private final GoogleMailSender googleMailSender;
    private final JwtTokenProvider jwtTokenProvider;

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
            userEmailAuthsEntity.updateCreatedAt(LocalDateTime.now());
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


    public void register(UserRegisterRequest request) throws NoSuchAlgorithmException {

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
                        .password(SHA256Util.getHashString(request.password()))
                        .status(UserStatus.ACTIVE)
                        .loginAttemptCnt(0)
                        .build()
        );

    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        UsersEntity usersEntity = jpaUsersRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(ErrorCode.USER_NOT_EXIST.getMessage()));

        User.UserBuilder builder = withUsername(usersEntity.getEmail());
        try {
            builder.password(SHA256Util.getHashString(usersEntity.getPassword()));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        return builder.build();
    }

    public UserClaims getUserClaims(String email){

        UsersEntity usersEntity = jpaUsersRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(ErrorCode.USER_NOT_EXIST.getMessage()));

        return UserClaims.builder()
                .userId(usersEntity.getId())
                .email(usersEntity.getEmail())
                .nickname(usersEntity.getNickname())
                .build();
    }

    public void login(UserLoginRequest request, HttpServletResponse response) throws NoSuchAlgorithmException {

        UsersEntity usersEntity = jpaUsersRepository.findByEmail(request.email())
                .orElseThrow(() -> new UsernameNotFoundException(ErrorCode.USER_NOT_EXIST.getMessage()));

        if (!usersEntity.isAccountNonExpired()) {
            throw new UserException(ErrorCode.USER_ALREADY_SIGNED_OUT);
        }

        if (!usersEntity.isAccountNonLocked()){
            throw new UserException(ErrorCode.USER_LOGIN_LOCKED);
        }

        if (!usersEntity.getPassword().equals(SHA256Util.getHashString(request.password()))) {
            usersEntity.updateLoginAttemptCnt(usersEntity.getLoginAttemptCnt() + 1);
            if (usersEntity.getLoginAttemptCnt() == 5) {
                usersEntity.updateStatus(BLOCKED);
            }
            jpaUsersRepository.save(usersEntity);
            throw new UserException(getLoginFailMessage(usersEntity.getLoginAttemptCnt()), ErrorCode.USER_LOGIN_FAIL);
        }

        UserClaims userClaims = UserClaims.builder()
                .userId(usersEntity.getId())
                .email(usersEntity.getEmail())
                .nickname(usersEntity.getNickname())
                .build();

        String token = jwtTokenProvider.createToken(userClaims);
        jwtTokenProvider.setCookieByJwtToken(response, token);
        usersEntity.updateLoginAttemptCnt(0);
        jpaUsersRepository.save(usersEntity);
    }

    private static String getLoginFailMessage(Integer loginAttemptCnt) {
        return ErrorCode.USER_LOGIN_FAIL.getMessage().replace("{loginAttemptCnt}", String.valueOf(loginAttemptCnt));
    }

    @Transactional
    public void updatePassword(UserPasswordUpdateRequest request) throws NoSuchAlgorithmException {

        UserEmailAuthsEntity userEmailAuthsEntity = jpaUserEmailAuthsRepository.findByEmail(request.email())
                .orElseThrow(() -> new UserException(ErrorCode.EMAIL_NOT_AUTHORIZED));

        if (!userEmailAuthsEntity.getCode().equals(request.code()) || Objects.isNull(userEmailAuthsEntity.getConfirmedAt())) {
            throw new UserException(ErrorCode.EMAIL_NOT_AUTHORIZED);
        }

        UsersEntity usersEntity = jpaUsersRepository.findByEmail(request.email())
                .orElseThrow(() -> new UsernameNotFoundException(ErrorCode.USER_NOT_EXIST.getMessage()));

        if (!usersEntity.isAccountNonExpired()) {
            throw new UserException(ErrorCode.USER_ALREADY_SIGNED_OUT);
        }

        if (!request.password().equals(request.rePassword())) {
            throw new UserException("Invalid Parameter. password and rePassword mismatch.", ErrorCode.INVALID_PARAMETER);
        }

        usersEntity.updatePassword(SHA256Util.getHashString(request.password()));
        usersEntity.updateLoginAttemptCnt(0);

        if (!usersEntity.isAccountNonLocked()){
            usersEntity.updateStatus(ACTIVE);
        }

    }
}
