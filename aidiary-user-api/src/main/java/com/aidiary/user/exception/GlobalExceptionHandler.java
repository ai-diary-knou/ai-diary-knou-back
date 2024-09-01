package com.aidiary.user.exception;

import com.aidiary.common.enums.ErrorCode;
import com.aidiary.common.enums.ErrorStatus;
import com.aidiary.common.exception.DiaryException;
import com.aidiary.common.exception.UserException;
import com.aidiary.common.vo.ResponseBundle.ErrorResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Objects;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        // Logging the exception
        log.error("Unknown Error ::", e);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(ErrorStatus.ERROR)
                .code(ErrorCode.UNKNOWN_ERROR.name())
                .message(e.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    @ExceptionHandler(UserException.class)
    public ResponseEntity<ErrorResponse> handleUserException(UserException e) {
        log.info("UserException Error ::", e);
        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(ErrorStatus.FAIL)
                .code(e.getErrorCode().name())
                .message(e.getMessage())
                .build();

        return ResponseEntity.status(e.getErrorCode().getCode()).body(errorResponse);
    }

    @ExceptionHandler(DiaryException.class)
    public ResponseEntity<ErrorResponse> handleDiaryException(DiaryException e) {
        log.info("DiaryException Error ::", e);
        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(ErrorStatus.FAIL)
                .code(e.getErrorCode().name())
                .message(e.getMessage())
                .build();

        return ResponseEntity.status(e.getErrorCode().getCode()).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException e) {
        log.info("MethodArgumentNotValidException Error ::", e);
        String errorMessage = e.getBindingResult().getFieldErrors().stream()
                .map(error -> Objects.requireNonNull(error.getDefaultMessage()))
                .findFirst().orElse("Invalid Parameter. Needs to check logs");

        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(ErrorStatus.FAIL)
                .code(ErrorCode.INVALID_PARAMETER.name())
                .message(errorMessage)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        log.info("MethodArgumentTypeMismatchException Error ::", e);
        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(ErrorStatus.FAIL)
                .code(ErrorCode.INVALID_PARAMETER.name())
                .message("Invalid argument type: " + e.getName())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorResponse> handleBindException(BindException e) {
        log.info("BindException Error ::", e);
        String errorMessage = Objects.requireNonNull(e.getFieldError()).getDefaultMessage();
        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(ErrorStatus.FAIL)
                .code(ErrorCode.INVALID_PARAMETER.name())
                .message(errorMessage)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException e) {
        log.info("ConstraintViolationException Error ::", e);
        String errorMessage = e.getMessage();
        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(ErrorStatus.FAIL)
                .code(ErrorCode.INVALID_PARAMETER.name())
                .message(errorMessage)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

}
