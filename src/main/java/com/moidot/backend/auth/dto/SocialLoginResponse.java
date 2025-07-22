package com.moidot.backend.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SocialLoginResponse {
    private String email;
    private String accessToken;
    private String refreshToken;
}
