package com.aidiary.common.enums;

import lombok.Getter;

/**
 * 일반적인 Http Error Code를 따르되,
 * 비즈니스 로직상의 에러는 600으로
 */
@Getter
public enum ErrorCode {

    // 공통 에러
    INVALID_PARAMETER(400, "Invalid Parameter. Please Check Request Documentation"),
    UNKNOWN_ERROR(500, "Unknown Server Error."),
    AUTH_CODE_EXPIRED(400, "Auth Code Expired. Please Try Again"),
    EMAIL_SEND_ERROR(600, "Email send error. Please check if email exists."),
    EMAIL_AUTH_FAIL(600, "Email Auth Failed. Please Try Again"),
    EMAIL_NOT_AUTHORIZED(401, "Email has not been authorized."),

    // 회원 관련
    USER_ALREADY_REGISTERED(600, "User already registered."),
    USER_REGISTER_FAIL(600, "User Register Failed. Please contact developer"),
    USER_NOT_EXIST(404, "User does not exist. Please check again."),
    USER_LOGIN_FAIL(401, "User Login Failed ({loginAttemptCnt}/5)"),
    USER_LOGIN_LOCKED(401, "User has been locked due to serial login failures."),
    USER_ALREADY_SIGNED_OUT(401, "User already signed out before. Please try another account."),
    USER_TOKEN_ERROR(500, "Cannot create or extract token info. Are you logged in?"),
    USER_LOGOUT_FAIL(500, "User log out error occurred. Are you logged in? Please Check before logout."),
    USER_AUTH_FAIL(403, "User Authentication Failed. Jwt Token Expired or Invalid."),

    // 일기 관련
    DIARY_NOT_FOUND(600, "Cannot find diary info."),
    DIARY_OWNER_MISMATCH(600, "Cannot access to diary."),
    DIARY_ALREADY_EXIST(600, "Diary already exists. Please update existing diary instead of registering."),
    DIARY_ANALYSIS_FAIL(600, "Diary Ai Analysis failed."),
    DIARY_REGISTER_FAIL(600, "Diary register failed."),
    DIARY_UPDATE_FAIL(600, "Diary update failed"),
    DIARY_PARSING_ERROR(600, "Diary parsing error. please contact backend developer.");

    private final Integer code;
    private final String message;

    ErrorCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

}
