package com.aidiary.user.application;

import com.aidiary.common.enums.ErrorCode;
import com.aidiary.common.exception.UserException;
import com.aidiary.common.vo.ResponseBundle.ResponseResult;
import com.aidiary.user.application.dto.UserRequestBundle.*;
import com.aidiary.user.application.service.UserService;
import com.aidiary.user.application.service.security.JwtTokenProvider;
import com.aidiary.user.domain.entity.UsersEntity;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/users")
@Slf4j
public class UserController {

    private final UserService userService;

    @GetMapping("/duplicate")
    public ResponseResult validateDuplicateEmail(UserValidateDuplicateRequest request){

        log.info("request : {}", request);
        userService.validateUserDuplication(request);

        return ResponseResult.success();
    }

    @PostMapping("/email/auth-code")
    public ResponseResult sendAuthCodeToEmail(@Valid @RequestBody UserEmailAuthCodeSentRequest request) throws MessagingException {

        log.info("request : {}", request);
        userService.createRandomCodeAndSendEmail(request);

        return ResponseResult.success();
    }

    @PostMapping("/email/auth")
    public ResponseResult confirmAuthCode(@Valid @RequestBody UserEmailAndAuthCode request) {

        log.info("request : {}", request);
        userService.confirmAuthCodeByEmail(request);

        return ResponseResult.success();

    }
    @PostMapping
    public ResponseResult register(@Valid @RequestBody UserRegisterRequest request){

        try {

            log.info("request : {}", request);
            userService.register(request);
            return ResponseResult.success();

        } catch (NoSuchAlgorithmException e) {

            log.info("비밀번호 암호화 하는 과정에서 오류 발생", e);
            throw new UserException(ErrorCode.UNKNOWN_ERROR);

        } catch (Exception e) {

            throw e;

        }

    }

    @PostMapping("/login")
    public ResponseResult login(@Valid @RequestBody UserLoginRequest request, HttpServletResponse response){

        try {
            log.info("request : {}", request);
            String accessToken = userService.login(request, response);
            return ResponseResult.success(accessToken);
        } catch (UserException e) {
            throw e;
        } catch (Exception e) {
            log.info("Login Failed :", e);
            throw new UserException("Login Failed by unknown reason.", ErrorCode.USER_LOGIN_FAIL);
        }

    }

    @GetMapping("/me")
    public ResponseResult getTokenUserClaims(@AuthenticationPrincipal UsersEntity usersEntity){

        try {
            log.info("user info : {}", usersEntity.getId());
            return ResponseResult.success(userService.getUserClaimsByUsersEntity(usersEntity));
        } catch (Exception e) {
            log.info("Token User Claims Not Found : {}", e);
            throw new UserException(ErrorCode.USER_TOKEN_ERROR);
        }

    }

    @PostMapping("/logout")
    public ResponseResult logout(HttpServletResponse response){

        try {
            userService.logout(response);
            return ResponseResult.success();
        } catch (Exception e) {
            log.info("Log out Fail : {}", e);
            throw new UserException(ErrorCode.USER_LOGOUT_FAIL);
        }

    }

    @PatchMapping("/password")
    public ResponseResult updatePassword(@Valid @RequestBody UserPasswordUpdateRequest request){

        try {

            log.info("request : {}", request);
            userService.updatePassword(request);
            return ResponseResult.success();

        } catch (UserException e) {
            throw e;
        } catch (Exception e) {
            log.info("Password update fail ::", e);
            throw new UserException(ErrorCode.UNKNOWN_ERROR);
        }

    }

}
