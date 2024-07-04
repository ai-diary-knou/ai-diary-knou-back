package com.aidiary.user.presentation.controller;

import com.aidiary.common.enums.ErrorCode;
import com.aidiary.common.exception.BaseException;
import com.aidiary.common.exception.UserException;
import com.aidiary.common.vo.ResponseBundle.ResponseResult;
import com.aidiary.user.application.dto.UserRequestBundle.*;
import com.aidiary.user.application.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/users")
@Slf4j
public class UserController {

    private final UserService userService;

    @PostMapping("/email/duplicate")
    public ResponseResult validateDuplicateEmail(@RequestBody UserEmailDuplicateValidateRequest request){

        userService.validateDuplicateEmail(request.email());

        return ResponseResult.success();
    }

    @PostMapping("/email/auth")
    public ResponseResult sendAuthCodeToEmail(@RequestBody UserEmailAuthCodeSentRequest request){

        throw new UserException(ErrorCode.UNKNOWN_ERROR);

    }

    @PostMapping("/email/auth/verify/{temporalType}")
    public ResponseResult verifyAuthCodeFromEmail(@PathVariable String temporalType){

        if ("expired".equals(temporalType)) {
            throw new UserException(ErrorCode.USER_EMAIL_AUTH_CODE_EXPIRED);
        }

        throw new UserException(ErrorCode.INVALID_PARAMETER);
    }

    @PostMapping("/nickname/duplicate")
    public ResponseResult validateDuplicateNickname(@RequestBody UserNicknameDuplicateValidateRequest request){

        throw new UserException(ErrorCode.USER_EMAIL_DUPLICATE);

    }



}
