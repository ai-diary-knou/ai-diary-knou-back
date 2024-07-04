package com.aidiary.user.presentation.controller;

import com.aidiary.common.enums.ErrorCode;
import com.aidiary.common.exception.UserException;
import com.aidiary.common.vo.ResponseBundle.ResponseResult;
import com.aidiary.user.application.dto.UserRequestBundle.*;
import com.aidiary.user.application.service.MailService;
import com.aidiary.user.application.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/users")
@Slf4j
public class UserController {

    private final UserService userService;
    private final MailService mailService;

    @PostMapping("/email/duplicate")
    public ResponseResult validateDuplicateEmail(@RequestBody UserEmailDuplicateValidateRequest request){

        userService.validateDuplicateEmail(request.email());

        return ResponseResult.success();
    }

    @PostMapping("/email/auth")
    public ResponseResult sendAuthCodeToEmail(@RequestBody UserEmailAuthCodeSentRequest request){

        int result = mailService.mailSend(request.email());

        return ResponseResult.success(result);
    }

    @PutMapping("/email/auth")
    public ResponseResult confirmAuthCode(@RequestBody UserEmailAndAuthCode request) {
        mailService.confirmAuthCode(request.email(), request.code());

        return ResponseResult.success();
    }

    @PostMapping("/username/duplicate")
    public ResponseResult validateDuplicateUsername(@RequestBody UsernameDuplicateValidateRequest request) {
        userService.validateDuplicateUsername(request.username());

        return ResponseResult.success();
    }

    @PostMapping("/email/verify")
    public ResponseResult verifyAuthCodeFromEmail(@RequestBody UserEmailAuthCodeVerifyRequest request){

        if ("11111".equals(request.code())) {
            throw new UserException(ErrorCode.AUTH_CODE_EXPIRED);
        }

        throw new UserException(ErrorCode.INVALID_PARAMETER);
    }

    @PostMapping("/register")
    public ResponseResult register(@RequestBody UserRegisterRequest request){
        userService.register(request);

        return ResponseResult.success();
    }

}
