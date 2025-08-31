package com.moidot.backend.auth.service;

import com.moidot.backend.auth.dto.SocialLoginRequest;
import com.moidot.backend.auth.dto.SocialLoginResponse;
import com.moidot.backend.auth.util.CookieUtil;
import com.moidot.backend.auth.util.JwtUtil;
import com.moidot.backend.auth.verify.SocialProvider;
import com.moidot.backend.auth.verify.VerifiedIdentity;
import com.moidot.backend.user.entity.User;
import com.moidot.backend.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;

@Slf4j
@Service
public class AuthService {

    private final JwtUtil jwtUtil;
    private final CookieUtil cookieUtil;
    private final SocialVerifyService socialVerifyService;
    private final UserRepository userRepository;

    public AuthService(JwtUtil jwtUtil, CookieUtil cookieUtil,
                       SocialVerifyService socialVerifyService, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.cookieUtil = cookieUtil;
        this.socialVerifyService = socialVerifyService;
        this.userRepository = userRepository;
    }

    public SocialLoginResponse socialLogin(SocialLoginRequest request, HttpServletResponse response) {
        // 1. Social Login access token 검증
        VerifiedIdentity verifiedIdentity = verifySocialInfo(request);

        // 2. 소셜 로그인 처리
        return handleSocialLogin(verifiedIdentity, response);
    }

    public VerifiedIdentity verifySocialInfo(SocialLoginRequest request) {
        // 문자열 - enum 반환
        SocialProvider providerEnum = resolveProvider(request.getProvider());

        // 여기서 실제 서버 검증 수행 (카카오/구글/네이버 중 자동 선택)
        VerifiedIdentity v = socialVerifyService.verify(providerEnum, request.getAccessToken());

        log.info("Verified provider={}, providerUserId={}, email={}", v.provider(), v.providerUserId(), v.email());
        return v;
    }

    /**
     * 문자열 입력을 안전하게 SocialProvider로 변환한다.
     * - 대소문자 무시
     * - 앞뒤 공백 제거
     * - 매칭 실패 시 BAD_REQUEST 예외
     */
    private SocialProvider resolveProvider(String provider) {
        try {
            // 대문자 변환
            return SocialProvider.valueOf(provider.trim().toUpperCase());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unsupported provider: " + provider);
        }
    }

    private SocialLoginResponse handleSocialLogin(VerifiedIdentity verifiedIdentity, HttpServletResponse response) {
        User user = userRepository.findUserByProviderUserIdAndProvider(
                verifiedIdentity.providerUserId(), verifiedIdentity.provider());

        Instant current = Instant.now();
        if (user == null) {
            // 새로운 사용자 생성
            user = new User(verifiedIdentity.providerUserId(), verifiedIdentity.provider(),
                    verifiedIdentity.email(), null, current, null);
            userRepository.save(user);

            log.info("New user created: {}", user.toString());

        } else {
            // 기존 사용자 업데이트 (마지막 로그인 시간 갱신)

            user.setLastLoginAt(current);
            userRepository.save(user);
            log.info("login user {} ", user.toString());
        }

        String providerUserId = user.getProviderUserId();
        SocialProvider provider = user.getProvider();
        String email = user.getEmail();

        // JWT 토큰 생성
        String accessToken = jwtUtil.generateAccessToken(providerUserId);
        String refreshToken = jwtUtil.generateRefreshToken(providerUserId);

        // 1. Authorization Header에 Access Token 추가
        response.setHeader("Authorization", "Bearer " + accessToken);

        // 2. Refresh Token을 HttpOnly 쿠키로 설정
        cookieUtil.addRefreshTokenCookie(response, refreshToken);

        return new SocialLoginResponse(provider, email);
    }

}
