package com.moidot.backend.global.exception;

import lombok.extern.slf4j.Slf4j;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = {BusinessException.class})
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex, HttpServletRequest request) {
        ErrorCode code = ex.getErrorCode();

        ErrorResponse response = new ErrorResponse(
                code.getStatus().value(),
                code.getCode(),
                code.getMessage(),
                request.getRequestURI(),     // 현재 요청의 path (동적으로 주입)
                Instant.now()                // 시간 (동적으로 주입)
        );

        log.info("Business Exception response = {}", response.toString());

        return new ResponseEntity<>(response, code.getStatus());
    }
}
