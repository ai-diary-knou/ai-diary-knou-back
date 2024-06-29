package com.aidiary.admin.presentation.exception;

import com.aidiary.common.exception.BaseException;
import com.aidiary.common.vo.ResponseBundle;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public ResponseBundle.ErrorResponse handleException(BaseException e){
        return ResponseBundle.ErrorResponse.of(e.getErrorCode(), e);
    }

}
