package com.aidiary.common.exception;

import com.aidiary.common.enums.ErrorCode;
import com.aidiary.common.vo.ResponseBundle;
import lombok.Getter;
import java.util.List;

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

    public DiaryException(ErrorCode errorCode, List<ResponseBundle.SubErrorResponse> errors) {
        super(errorCode, errors);
    }

    public DiaryException(String message, ErrorCode errorCode, List<ResponseBundle.SubErrorResponse> errors) {
        super(message, errorCode, errors);
    }

    public DiaryException(String message, Throwable cause, ErrorCode errorCode, List<ResponseBundle.SubErrorResponse> errors) {
        super(message, cause, errorCode, errors);
    }

    public DiaryException(Throwable cause, ErrorCode errorCode, List<ResponseBundle.SubErrorResponse> errors) {
        super(cause, errorCode, errors);
    }

    public DiaryException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, ErrorCode errorCode, List<ResponseBundle.SubErrorResponse> errors) {
        super(message, cause, enableSuppression, writableStackTrace, errorCode, errors);
    }
}
