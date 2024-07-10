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
    AUTH_CODE_EXPIRED(600, "Auth Code Expired. Please Try Again"),

    // 회원 관련
    USER_ALREADY_REGISTERED(600, "User already registered. Please use another email or nickname"),
    MISMATCH_PASSWORD(700, "Mismatch Password. Please Try Again");


    private final Integer code;
    private final String message;

    ErrorCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

}
