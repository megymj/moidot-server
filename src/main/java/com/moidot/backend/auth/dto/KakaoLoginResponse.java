package com.moidot.backend.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class KakaoLoginResponse {

    // 🔐 Token 관련
    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("token_type")
    private String tokenType;

    @JsonProperty("expires_in")
    private int expiresIn;

    @JsonProperty("refresh_token")
    private String refreshToken;

    @JsonProperty("refresh_token_expires_in")
    private int refreshTokenExpiresIn;

    @JsonProperty("scope")
    private String scope;

    // 👤 사용자 정보
    private Long id;
    private String nickname;
    private String email;
    private String profileImage;

    // === Getter & Setter 생략 ===

    public static KakaoLoginResponse from(KakaoTokenResponse token, KakaoUserResponse user) {
        KakaoLoginResponse res = new KakaoLoginResponse();
        res.tokenType = token.getTokenType();
        res.accessToken = token.getAccessToken();
        res.expiresIn = token.getExpiresIn();
        res.refreshToken = token.getRefreshToken();
        res.refreshTokenExpiresIn = token.getRefreshTokenExpiresIn();
//        res.scope = token.getScope();

        res.id = user.getId();
        res.nickname = user.getKakaoAccount().getProfile().getNickname();
        res.email = user.getKakaoAccount().getEmail();
        res.profileImage = user.getKakaoAccount().getProfile().getProfileImageUrl();

        return res;
    }
}
