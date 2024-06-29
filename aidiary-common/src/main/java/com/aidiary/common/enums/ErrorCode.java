package com.aidiary.common.enums;

import lombok.Getter;

@Getter
public enum ErrorCode {

    INVALID_PARAMETER(400, "Invalid Parameter. Please Check Request Documentation"),
    UNKNOWN_ERROR(500, "Unknown Server Error."),
    USER_EMAIL_DUPLICATE(400, "Email Already Registered"),
    USER_NICKNAME_DUPLICATE(400, "Nickname Already Taken"),
    USER_EMAIL_AUTH_CODE_EXPIRED(500, "Email Auth Code Expired. Please Try Again");

    private final Integer code;
    private final String message;

    ErrorCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

}
