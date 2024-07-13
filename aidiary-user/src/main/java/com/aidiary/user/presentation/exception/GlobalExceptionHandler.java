package com.aidiary.user.presentation.exception;

import com.aidiary.common.enums.ErrorCode;
import com.aidiary.common.enums.ErrorStatus;
import com.aidiary.common.exception.UserException;
import com.aidiary.common.vo.ResponseBundle.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Objects;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(UserException.class)
    public ErrorResponse handleUserException(UserException e){
        return ErrorResponse.builder()
                .status(ErrorStatus.FAIL)
                .code(e.getErrorCode().name())
                .message(e.getMessage())
                .build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponse handleValidationException(MethodArgumentNotValidException e) {

        String errorMessage = e.getBindingResult().getFieldErrors().stream()
                .map(error -> Objects.requireNonNull(error.getDefaultMessage()))
                .findFirst().orElse("Invalid Parameter. Needs to check logs");

        return ErrorResponse.builder()
                .status(ErrorStatus.FAIL)
                .code(ErrorCode.INVALID_PARAMETER.name())
                .message(errorMessage)
                .build();
    }

}
