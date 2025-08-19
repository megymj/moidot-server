package com.moidot.backend.auth.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Slf4j
@Component
public class CookieUtil {

    public void addRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        /*Cookie cookie = new Cookie("refreshTokenByServer", refreshToken);
        cookie.setHttpOnly(true);  // JS 접근 차단
//        cookie.setSecure(true);    // HTTPS 전송만 허용
        cookie.setSecure(false); // 개발 환경에서는 false로 설정, 실제 서비스에서는 true로 변경 필요
        cookie.setPath("/");       // 모든 요청에 포함
        cookie.setMaxAge(60 * 60 * 24 * 14); // 14일
        response.addCookie(cookie);*/

        ResponseCookie cookie = ResponseCookie.from("m_refreshToken", refreshToken)
                .httpOnly(true) // JS에서 접근 차단. 개발환경에서는 false, 실제 서비스에서는 true로 변경 필요
                .secure(true) // 개발환경 HTTP에선 무조건 false. HTTPS 환경에서만 전송 (운영환경 필수)
                .path("/")      // 전체 경로에 대해 쿠키 유효
                .maxAge(Duration.ofDays(14))
                .sameSite("None") // Cross-Origin 허용 (프론트 ↔ 백엔드 도메인 다를 때 필수)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}
