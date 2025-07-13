package com.moidot.backend.auth.controller;

import com.moidot.backend.auth.dto.KakaoLoginResponse;
import com.moidot.backend.auth.service.KakaoOAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class OAuthController {

    private final KakaoOAuthService kakaoOAuthService;


    @GetMapping("/callback/kakao")
    public KakaoLoginResponse kakaoCallback(@RequestParam("code") String code,
                                            @RequestParam("state") String state) {

        return kakaoOAuthService.loginWithKakao(code);
    }

}
