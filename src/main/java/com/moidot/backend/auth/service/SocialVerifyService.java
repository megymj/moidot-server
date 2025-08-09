package com.moidot.backend.auth.service;

import com.moidot.backend.auth.verify.SocialProvider;
import com.moidot.backend.auth.verify.SocialVerifier;
import com.moidot.backend.auth.verify.VerifiedIdentity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SocialVerifyService {

    private final Map<SocialProvider, SocialVerifier> byProvider;

    // 스프링이 모든 구현체(KakaoVerifier, GoogleVerifier, ...)를 List로 주입
    public SocialVerifyService(List<SocialVerifier> verifiers) {
        this.byProvider = verifiers.stream()
                .collect(Collectors.toMap(SocialVerifier::provider, Function.identity()));
    }

    public VerifiedIdentity verify(SocialProvider provider, String accessToken) {
        SocialVerifier v = byProvider.get(provider);
        if (v == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unsupported provider: " + provider);
        }
        return v.verify(accessToken);
    }
}
