package com.aidiary.user.presentation.exception;

import com.aidiary.common.enums.ErrorStatus;
import com.aidiary.common.exception.UserException;
import com.aidiary.common.vo.ResponseBundle.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(UserException.class)
    public ErrorResponse handleException(UserException e){
        return ErrorResponse.builder()
                .status(ErrorStatus.FAIL)
                .code(e.getErrorCode().name())
                .message(e.getMessage())
                .build();
    }

}
