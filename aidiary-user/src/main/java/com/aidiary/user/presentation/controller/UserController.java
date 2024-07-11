package com.aidiary.user.presentation.controller;

import com.aidiary.common.enums.ErrorCode;
import com.aidiary.common.exception.UserException;
import com.aidiary.common.vo.ResponseBundle.ResponseResult;
import com.aidiary.user.application.dto.UserRequestBundle.*;
import com.aidiary.user.application.service.UserService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

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
    public ResponseResult sendAuthCodeToEmail(@RequestBody UserEmailAuthCodeSentRequest request) throws MessagingException {

        userService.createRandomCodeAndSendEmail(request.email());

        return ResponseResult.success();
    }

    @PostMapping("/email/auth")
    public ResponseResult confirmAuthCode(@RequestBody UserEmailAndAuthCode request) {
        //mailService.confirmAuthCode(request.email(), request.code());

        return ResponseResult.success();

    }

    @PostMapping("/email/verify")
    public ResponseResult verifyAuthCodeFromEmail(@RequestBody UserEmailAuthCodeVerifyRequest request){

        if ("11111".equals(request.code())) {
            throw new UserException(ErrorCode.AUTH_CODE_EXPIRED);
        }

        throw new UserException(ErrorCode.INVALID_PARAMETER);
    }

    @PostMapping
    public ResponseResult register(@RequestBody UserRegisterRequest request){


        return ResponseResult.success();
    }

    @PostMapping("/login")
    public ResponseResult login(@RequestBody UserLoginRequest request){

        throw new UserException(ErrorCode.USER_LOGIN_FAIL);

    }

    @PatchMapping
    public ResponseResult updatePassword(@RequestBody UserPasswordUpdateRequest request){

        throw new UserException(ErrorCode.USER_LOGIN_FAIL);

        //return ResponseResult.success();
    }

}
