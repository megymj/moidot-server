package com.moidot.backend.auth.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

@Component
public class CookieUtil {

    public void addRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        Cookie cookie = new Cookie("refreshToken", refreshToken);
        cookie.setHttpOnly(true);  // JS 접근 차단
//        cookie.setSecure(true);    // HTTPS 전송만 허용
        cookie.setPath("/");       // 모든 요청에 포함
        cookie.setMaxAge(60 * 60 * 24 * 14); // 14일
        response.addCookie(cookie);
    }
}
