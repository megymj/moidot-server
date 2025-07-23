package com.moidot.backend.auth.service;

import com.moidot.backend.auth.dto.SocialLoginRequest;
import com.moidot.backend.auth.dto.SocialLoginResponse;
import com.moidot.backend.auth.util.CookieUtil;
import com.moidot.backend.auth.util.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final JwtUtil jwtUtil;
    private final CookieUtil cookieUtil;

    public AuthService(JwtUtil jwtUtil, CookieUtil cookieUtil) {
        this.jwtUtil = jwtUtil;
        this.cookieUtil = cookieUtil;
    }
    public SocialLoginResponse kakaoLogin(SocialLoginRequest request, HttpServletResponse response) {
        return handleSocialLogin(request, response);
    }

    private SocialLoginResponse handleSocialLogin(SocialLoginRequest request, HttpServletResponse response) {
//        Optional<User> userOptional = userRepository.findByEmail(request.getEmail());
//        User user;
//
//        if (userOptional.isPresent()) {
//            user = userOptional.get();
//        } else {
//            user = new User(request.getEmail(), request.getNickname(), request.getProfileImage());
//            userRepository.save(user);
//        }

        String email = request.getEmail();

        // JWT 토큰 생성
        String accessToken = jwtUtil.generateAccessToken(email);
        String refreshToken = jwtUtil.generateRefreshToken(email);

//        user.setRefreshToken(refreshToken);
//        userRepository.save(user);

        // 1. Authorization Header에 Access Token 추가
        response.setHeader("Authorization", "Bearer " + accessToken);

        // 2. Refresh Token을 HttpOnly 쿠키로 설정
        cookieUtil.addRefreshTokenCookie(response, refreshToken);

        return new SocialLoginResponse(email);
    }

}
