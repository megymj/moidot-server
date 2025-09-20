package com.moidot.backend.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // auth

    REFRESH_TOKEN_NOT_EXISTED(HttpStatus.UNAUTHORIZED, "A4013", "프론트에서 넘어온 Cookie에 m_refreshToken 값이 존재하지 않습니다");




    /**
     * HTTP 상태 코드 (예: 400, 404, 500)
     */
    private final HttpStatus status;

    /**
     * 프론트로 전달되는 에러 코드 (예: 4011, 4012 등)
     */
    private final String code;

    /**
     * 에러 상세 설명 또는 디버깅용
     */
    private final String message;

}
