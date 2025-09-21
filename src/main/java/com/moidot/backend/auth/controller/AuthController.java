package com.moidot.backend.auth.controller;

import com.moidot.backend.auth.dto.SocialLoginRequest;
import com.moidot.backend.auth.dto.SocialLoginResponse;
import com.moidot.backend.auth.service.AuthService;
import com.moidot.backend.global.exception.BusinessException;
import com.moidot.backend.global.exception.ErrorCode;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/social-login")
    public ResponseEntity<SocialLoginResponse> socialLogin(@RequestBody SocialLoginRequest request,
                                                           HttpServletResponse response) {

        return ResponseEntity.ok(authService.socialLogin(request, response));
    }

    @PostMapping("/refresh")
    public Object reissueRefreshToken(@CookieValue(value = "m_refreshToken", required = false) String refreshToken,
                                                             HttpServletResponse response) {

        if (refreshToken == null || refreshToken.isBlank()) {
            // 프론트에서 로그아웃 처리 필요
            throw new BusinessException(ErrorCode.REFRESH_TOKEN_MISSING);
        }

        boolean result = authService.reissueRefreshToken(refreshToken, response);
        if (result) {
            return ResponseEntity.ok(HttpStatus.OK);
        } else {
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}
