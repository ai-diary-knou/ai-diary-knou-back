package com.aidiary.admin.presentation.exception;

import com.aidiary.common.enums.ErrorCode;
import com.aidiary.common.exception.BaseException;
import com.aidiary.common.vo.ResponseBundle;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static com.aidiary.common.enums.ErrorCode.INVALID_PARAMETER;
import static com.aidiary.common.enums.ErrorCode.UNKNOWN_ERROR;


@Slf4j
@RequiredArgsConstructor
@Order(-2)
public class GlobalExceptionHandler implements ErrorWebExceptionHandler {

    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable exception) {

        log.info("GlobalExceptionHandler Begin : ");
        ServerHttpResponse response = exchange.getResponse();

        if (response.isCommitted()) {
            return Mono.error(exception);
        }

        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        ErrorCode errorCode = errorCodeOf(exception);
        response.setStatusCode(HttpStatusCode.valueOf(errorCode.getCode()));

        return response.writeWith(Mono.fromSupplier(() -> {

            DataBufferFactory bufferFactory = response.bufferFactory();

            try {

                ResponseBundle.ErrorResponse errorResponse = ResponseBundle.ErrorResponse.of(errorCode);
                byte[] binaryErrorResponse = objectMapper.writeValueAsBytes(errorResponse);
                log.info("GlobalExceptionHandler Info : ", exception);
                return bufferFactory.wrap(binaryErrorResponse);

            } catch (Exception e) {

              log.error("GlobalExceptionHandler Error : ", e);
              return bufferFactory.wrap(new byte[0]);

            }
        }));
    }

    private static ErrorCode errorCodeOf(Throwable exception) {
        if (exception instanceof BaseException) {
            return ((BaseException) exception).getErrorCode();
        } else if (exception instanceof BadRequestException){
            return INVALID_PARAMETER;
        }
        return  UNKNOWN_ERROR;
    }
}
