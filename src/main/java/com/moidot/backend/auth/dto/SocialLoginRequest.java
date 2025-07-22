package com.moidot.backend.auth.dto;

import lombok.Getter;

@Getter
public class SocialLoginRequest {
    private String provider;      // kakao, google
    private String email;

}
