package com.aidiary.user.presentation.exception;

import com.aidiary.common.exception.UserException;
import com.aidiary.common.vo.ResponseBundle;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(UserException.class)
    public ResponseBundle.ErrorResponse handleException(UserException e){
        return ResponseBundle.ErrorResponse.of(e.getErrorCode(), e);
    }

}
