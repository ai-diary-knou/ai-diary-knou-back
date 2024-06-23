package com.aidiary.common.vo;

import com.aidiary.common.enums.ErrorCode;
import com.aidiary.common.exception.BaseException;
import lombok.Builder;

import java.util.ArrayList;
import java.util.List;

public abstract class ResponseBundle {

    @Builder
    public record ResponseResult(Integer code, String message, String status, Object data) {

        public static ResponseResult success(String message, Object data){
            return ResponseResult.builder()
                    .code(200)
                    .message(message)
                    .status("SUCCESS")
                    .data(data)
                    .build();
        }

        public static ResponseResult success(Object data){
            return ResponseResult.builder()
                    .code(200)
                    .message("")
                    .status("SUCCESS")
                    .data(data)
                    .build();
        }

    }

    @Builder
    public record ErrorResponse (Integer code, String message, String status, List<SubErrorResponse> errors){

        public static ErrorResponse of(ErrorCode errorCode, Throwable exception) {
            return ErrorResponse
                    .builder()
                    .code(errorCode.getCode())
                    .message(errorCode.getMessage())
                    .status(errorCode.name())
                    .errors(exception instanceof BaseException ? ((BaseException) exception).getErrors() : new ArrayList<>())
                    .build();
        }

    }

    @Builder
    public record SubErrorResponse(String domain, String status, String message) {

    }

}
