package com.aidiary.common.exception;

import com.aidiary.common.enums.ErrorCode;
import lombok.Getter;

@Getter
public class DiaryException extends BaseException{

    public DiaryException(ErrorCode errorCode) {
        super(errorCode);
    }

    public DiaryException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }

    public DiaryException(String message, Throwable cause, ErrorCode errorCode) {
        super(message, cause, errorCode);
    }

    public DiaryException(Throwable cause, ErrorCode errorCode) {
        super(cause, errorCode);
    }

    public DiaryException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, ErrorCode errorCode) {
        super(message, cause, enableSuppression, writableStackTrace, errorCode);
    }
}
