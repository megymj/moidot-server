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
                .httpOnly(true) // 개발환경에서는 false, 실제 서비스에서는 true로 변경 필요
                .secure(false) // 개발환경 HTTP에선 무조건 false
                .path("/")
                .maxAge(Duration.ofDays(14))
                .sameSite("Lax") // 로컬 개발환경에 적합: TODO.. 추후 학습 필요
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}
