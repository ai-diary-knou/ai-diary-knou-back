package com.aidiary.user.presentation.controller;

import com.aidiary.common.enums.ErrorCode;
import com.aidiary.common.exception.UserException;
import com.aidiary.common.vo.ResponseBundle.ResponseResult;
import com.aidiary.user.application.dto.UserRequestBundle.*;
import com.aidiary.user.application.service.UserService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;
import java.security.Principal;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/users")
@Slf4j
public class UserController {

    private final UserService userService;

    @GetMapping("/duplicate")
    public ResponseResult validateDuplicateEmail(UserValidateDuplicateRequest request){

        userService.validateUserDuplication(request);

        return ResponseResult.success();
    }

    @PostMapping("/email/auth-code")
    public ResponseResult sendAuthCodeToEmail(@Valid @RequestBody UserEmailAuthCodeSentRequest request) throws MessagingException {

        userService.createRandomCodeAndSendEmail(request);

        return ResponseResult.success();
    }

    @PostMapping("/email/auth")
    public ResponseResult confirmAuthCode(@Valid @RequestBody UserEmailAndAuthCode request) {

        userService.confirmAuthCodeByEmail(request);

        return ResponseResult.success();

    }
    @PostMapping
    public ResponseResult register(@Valid @RequestBody UserRegisterRequest request){

        try {

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
            userService.login(request, response);
            return ResponseResult.success();
        } catch (UserException e) {
            throw e;
        } catch (Exception e) {
            log.info("Login Failed :", e);
            throw new UserException("Login Failed by unknown reason.", ErrorCode.USER_LOGIN_FAIL);
        }

    }

    @PatchMapping("/password")
    public ResponseResult updatePassword(Principal principal, @Valid @RequestBody UserPasswordUpdateRequest request){

        try {

            userService.updatePassword(principal.getName(), request);
            return ResponseResult.success();

        } catch (UserException e) {
            throw e;
        } catch (Exception e) {
            log.info("Password update fail ::", e);
            throw new UserException(ErrorCode.UNKNOWN_ERROR);
        }

    }

}
