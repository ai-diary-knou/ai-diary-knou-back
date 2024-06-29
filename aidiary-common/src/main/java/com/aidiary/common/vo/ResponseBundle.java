package com.aidiary.common.vo;

import com.aidiary.common.enums.ErrorCode;
import com.aidiary.common.enums.ErrorStatus;
import com.aidiary.common.exception.BaseException;
import lombok.Builder;

import static com.aidiary.common.enums.ErrorCode.UNKNOWN_ERROR;

public abstract class ResponseBundle {

    @Builder
    public record ResponseResult(ErrorStatus status, Object data) {

        public static ResponseResult success(String message, Object data){
            return ResponseResult.builder()
                    .status(ErrorStatus.SUCCESS)
                    .data(data)
                    .build();
        }

        public static ResponseResult success(Object data){
            return ResponseResult.builder()
                    .status(ErrorStatus.SUCCESS)
                    .data(data)
                    .build();
        }

        public static ResponseResult success(){
            return ResponseResult.builder()
                    .status(ErrorStatus.SUCCESS)
                    .data(null)
                    .build();
        }

    }

    @Builder
    public record ErrorResponse (ErrorStatus status, String code, String message){

        public static ErrorResponse of(ErrorCode errorCode, Throwable exception) {
            return ErrorResponse
                    .builder()
                    .status(exception instanceof BaseException ? ErrorStatus.FAIL : ErrorStatus.ERROR)
                    .code(errorCode.name())
                    .message(errorCode.getMessage())
                    .build();
        }

    }

}
