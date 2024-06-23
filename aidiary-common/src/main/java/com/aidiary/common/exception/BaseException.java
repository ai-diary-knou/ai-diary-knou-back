package com.aidiary.common.exception;

import com.aidiary.common.enums.ErrorCode;
import com.aidiary.common.vo.ResponseBundle;
import lombok.Getter;

import java.util.List;

@Getter
public class BaseException extends RuntimeException{

    private ErrorCode errorCode;

    private List<ResponseBundle.SubErrorResponse> errors;

    public BaseException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public BaseException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public BaseException(String message, Throwable cause, ErrorCode errorCode) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public BaseException(Throwable cause, ErrorCode errorCode) {
        super(cause);
        this.errorCode = errorCode;
    }

    public BaseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, ErrorCode errorCode) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.errorCode = errorCode;
    }

    public BaseException(ErrorCode errorCode, List<ResponseBundle.SubErrorResponse> errors) {
        this.errorCode = errorCode;
        this.errors = errors;
    }

    public BaseException(String message, ErrorCode errorCode, List<ResponseBundle.SubErrorResponse> errors) {
        super(message);
        this.errorCode = errorCode;
        this.errors = errors;
    }

    public BaseException(String message, Throwable cause, ErrorCode errorCode, List<ResponseBundle.SubErrorResponse> errors) {
        super(message, cause);
        this.errorCode = errorCode;
        this.errors = errors;
    }

    public BaseException(Throwable cause, ErrorCode errorCode, List<ResponseBundle.SubErrorResponse> errors) {
        super(cause);
        this.errorCode = errorCode;
        this.errors = errors;
    }

    public BaseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, ErrorCode errorCode, List<ResponseBundle.SubErrorResponse> errors) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.errorCode = errorCode;
        this.errors = errors;
    }
}
