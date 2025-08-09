package com.moidot.backend.auth.verify;

public interface SocialVerifier {

    SocialProvider provider();             // KAKAO, GOOGLE, NAVER, ...

    VerifiedIdentity verify(String accessToken);
}
