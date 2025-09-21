package com.moidot.backend.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // ========================
    // Common
    // ========================
    INTERNAL_SERVER_ERROR("COMMON_500_01", HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다."),


    // ========================
    // Auth
    // ========================
    REFRESH_TOKEN_MISSING("AUTH_401_01", HttpStatus.UNAUTHORIZED, "Refresh Token이 존재하지 않습니다."),
    INVALID_JWT_TOKEN("AUTH_401_02", HttpStatus.UNAUTHORIZED, "유효하지 않은 JWT Refresh 토큰입니다."),
    REFRESH_TOKEN_EXPIRED("AUTH_401_03", HttpStatus.UNAUTHORIZED, "Refresh Token이 만료되었습니다."),
    REFRESH_TOKEN_NOT_FOUND("AUTH_401_04", HttpStatus.UNAUTHORIZED, "서버에 Refresh Token 정보가 존재하지 않습니다."),
    PROVIDER_NOT_SUPPORTED("AUTH_400_01", HttpStatus.BAD_REQUEST, "지원하지 않는 Provider입니다.");





    /**
     * 프론트로 전달되는 에러 코드 (예: 4011, 4012 등)
     */
    private final String code;

    /**
     * HTTP 상태 코드 (예: 400, 404, 500)
     */
    private final HttpStatus status;

    /**
     * 에러 상세 설명 또는 디버깅용
     */
    private final String message;

}
