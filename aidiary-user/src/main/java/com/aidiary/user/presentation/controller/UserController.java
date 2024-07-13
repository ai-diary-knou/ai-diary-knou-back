package com.aidiary.user.presentation.controller;

import com.aidiary.common.enums.ErrorCode;
import com.aidiary.common.exception.UserException;
import com.aidiary.common.vo.ResponseBundle.ResponseResult;
import com.aidiary.user.application.dto.UserRequestBundle.*;
import com.aidiary.user.application.service.UserService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

            log.info("알 수 없는 에러가 발생했습니다.", e);
            throw new UserException(ErrorCode.UNKNOWN_ERROR);

        }

    }

    @PostMapping("/login")
    public ResponseResult login(@Valid @RequestBody UserLoginRequest request){

        throw new UserException(ErrorCode.USER_LOGIN_FAIL);

    }

    @PatchMapping
    public ResponseResult updatePassword(@Valid @RequestBody UserPasswordUpdateRequest request){

        throw new UserException(ErrorCode.USER_LOGIN_FAIL);

        //return ResponseResult.success();
    }

}
