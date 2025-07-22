package com.moidot.backend.auth.controller;

import com.moidot.backend.auth.dto.SocialLoginRequest;
import com.moidot.backend.auth.dto.SocialLoginResponse;
import com.moidot.backend.auth.service.AuthService;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<SocialLoginResponse> socialLogin(@RequestBody SocialLoginRequest request) {
        if ("kakao".equalsIgnoreCase(request.getProvider())) {
            return ResponseEntity.ok(authService.kakaoLogin(request));
        }
//        else if ("google".equalsIgnoreCase(request.getProvider())) {
//            return ResponseEntity.ok(authService.googleLogin(request));
//        }
        throw new IllegalArgumentException("Unsupported provider: " + request.getProvider());
    }
}
