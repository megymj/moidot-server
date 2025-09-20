package com.moidot.backend.auth.controller;

import com.moidot.backend.auth.dto.SocialLoginRequest;
import com.moidot.backend.auth.dto.SocialLoginResponse;
import com.moidot.backend.auth.service.AuthService;
import com.moidot.backend.global.exception.BusinessException;
import com.moidot.backend.global.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

//        throw new IllegalArgumentException("Unsupported provider: " + request.getProvider());
    }

    @PostMapping("/refresh")
    public Object reissueRefreshToken(@CookieValue(value = "m_refreshToken", required = false) String refreshToken,
                                                             HttpServletRequest request,
                                                             HttpServletResponse response) {

        if (refreshToken == null || refreshToken.isBlank()) {
            // 프론트에서 로그아웃 처리 필요
            throw new BusinessException(ErrorCode.REFRESH_TOKEN_NOT_EXISTED);
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//                    .body("Refresh token is missing.");
        }

        try {
            authService.reissueRefreshToken(refreshToken, response);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid or expired refresh token.");
        }

    }
}
