package com.aidiary.user.controller;

import com.aidiary.common.enums.ErrorCode;
import com.aidiary.common.exception.UserException;
import com.aidiary.common.vo.ResponseBundle.UserPrincipal;
import com.aidiary.common.vo.ResponseBundle.ResponseResult;
import com.aidiary.user.model.UserRequestBundle.*;
import com.aidiary.user.service.UserEmailAuthService;
import com.aidiary.user.service.UserLoginService;
import com.aidiary.user.service.UserRegisterService;
import com.aidiary.user.service.UserInfoService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/users")
@Slf4j
public class UserController {

    private final UserInfoService userInfoService;
    private final UserRegisterService userRegisterService;
    private final UserLoginService userLoginService;
    private final UserEmailAuthService userEmailAuthService;

    @GetMapping("/duplicate")
    public ResponseResult validateDuplicateEmail(DuplicateUserValidateRequest request){

        userInfoService.validateUserDuplication(request);

        return ResponseResult.success();
    }

    @PostMapping("/email/auth-code")
    public ResponseResult sendAuthCodeToEmail(@Valid @RequestBody UserEmailAuthCodeSentRequest request) {

        userEmailAuthService.createRandomCodeAndSendEmail(request);

        return ResponseResult.success();
    }

    @PostMapping("/email/auth")
    public ResponseResult confirmAuthCode(@Valid @RequestBody UserEmailAndAuthCode request) {

        userEmailAuthService.confirmEmailAuthCode(request);

        return ResponseResult.success();

    }

    @PostMapping
    public ResponseResult register(@Valid @RequestBody UserRegisterRequest request){

        try {

            userRegisterService.register(request);
            return ResponseResult.success();

        } catch (NoSuchAlgorithmException e) {

            log.info("비밀번호 암호화 하는 과정에서 오류 발생", e);
            throw new UserException(ErrorCode.UNKNOWN_ERROR);

        }

    }

    @PostMapping("/login")
    public ResponseResult login(@Valid @RequestBody UserLoginRequest request, HttpServletRequest httpServletRequest){

        try {
            String accessToken = userLoginService.login(request, httpServletRequest);
            return ResponseResult.success(accessToken);
        } catch (UserException e) {
            throw e;
        } catch (Exception e) {
            log.info("Login Failed :", e);
            throw new UserException("Login Failed by unknown reason.", ErrorCode.USER_LOGIN_FAIL);
        }

    }

    @GetMapping("/me")
    public ResponseResult getTokenUserClaims(@RequestAttribute("userPrincipal") UserPrincipal userPrincipal){

        try {
            return ResponseResult.success(Objects.requireNonNull(userPrincipal));
        } catch (Exception e) {
            log.info("Token User Claims Not Found ::", e);
            throw new UserException(ErrorCode.USER_TOKEN_ERROR);
        }

    }

    @PatchMapping("/password")
    public ResponseResult updatePassword(@Valid @RequestBody UserPasswordUpdateRequest request){

        try {

            log.info("request : {}", request);
            userInfoService.updatePassword(request);
            return ResponseResult.success();

        } catch (UserException e) {
            throw e;
        } catch (Exception e) {
            log.info("Password update fail ::", e);
            throw new UserException(ErrorCode.UNKNOWN_ERROR);
        }

    }

    @PutMapping("/nickname")
    public ResponseResult updateNickname(@RequestAttribute("userPrincipal") UserPrincipal userPrincipal,
                                         @Valid @RequestBody UserNicknameUpdateRequest request){

        userInfoService.updateNickname(userPrincipal.userId(), request);

        return ResponseResult.success();
    }

}
