package com.aidiary.common.exception;

import com.aidiary.common.enums.ErrorCode;
import com.aidiary.common.vo.ResponseBundle;
import lombok.Getter;

import java.util.List;

@Getter
public class UserException extends BaseException{

    public UserException(ErrorCode errorCode) {
        super(errorCode);
    }

    public UserException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }

    public UserException(String message, Throwable cause, ErrorCode errorCode) {
        super(message, cause, errorCode);
    }

    public UserException(Throwable cause, ErrorCode errorCode) {
        super(cause, errorCode);
    }

    public UserException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, ErrorCode errorCode) {
        super(message, cause, enableSuppression, writableStackTrace, errorCode);
    }

    public UserException(ErrorCode errorCode, List<ResponseBundle.SubErrorResponse> errors) {
        super(errorCode, errors);
    }

    public UserException(String message, ErrorCode errorCode, List<ResponseBundle.SubErrorResponse> errors) {
        super(message, errorCode, errors);
    }

    public UserException(String message, Throwable cause, ErrorCode errorCode, List<ResponseBundle.SubErrorResponse> errors) {
        super(message, cause, errorCode, errors);
    }

    public UserException(Throwable cause, ErrorCode errorCode, List<ResponseBundle.SubErrorResponse> errors) {
        super(cause, errorCode, errors);
    }

    public UserException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, ErrorCode errorCode, List<ResponseBundle.SubErrorResponse> errors) {
        super(message, cause, enableSuppression, writableStackTrace, errorCode, errors);
    }
}
