package com.aidiary.user.presentation.controller;

import com.aidiary.common.enums.ErrorCode;
import com.aidiary.common.exception.UserException;
import com.aidiary.common.vo.ResponseBundle.ResponseResult;
import com.aidiary.user.application.dto.UserRequestBundle.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@Slf4j
public class UserController {

    @GetMapping("/duplicate")
    public ResponseResult validateDuplicateEmail(UserValidateDuplicateRequest request){

        throw new UserException(ErrorCode.USER_ALREADY_REGISTERED);

    }

    @PostMapping("/email/auth")
    public ResponseResult sendAuthCodeToEmail(@RequestBody UserEmailAuthCodeSentRequest request){

        throw new UserException(ErrorCode.UNKNOWN_ERROR);

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

}
