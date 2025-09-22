package com.moidot.backend.auth.service;

import com.moidot.backend.auth.domain.RefreshToken;
import com.moidot.backend.auth.dto.SocialLoginRequest;
import com.moidot.backend.auth.dto.SocialLoginResponse;
import com.moidot.backend.auth.repository.RefreshTokenRepository;
import com.moidot.backend.auth.util.CookieUtil;
import com.moidot.backend.auth.util.JwtUtil;
import com.moidot.backend.auth.verify.SocialProvider;
import com.moidot.backend.auth.verify.VerifiedIdentity;
import com.moidot.backend.global.exception.BusinessException;
import com.moidot.backend.global.exception.ErrorCode;
import com.moidot.backend.user.entity.User;
import com.moidot.backend.user.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Slf4j
@Service
public class AuthService {

    private final JwtUtil jwtUtil;
    private final CookieUtil cookieUtil;
    private final SocialVerifyService socialVerifyService;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    public AuthService(JwtUtil jwtUtil, CookieUtil cookieUtil,
                       SocialVerifyService socialVerifyService, UserRepository userRepository,
                       RefreshTokenRepository refreshTokenRepository) {
        this.jwtUtil = jwtUtil;
        this.cookieUtil = cookieUtil;
        this.socialVerifyService = socialVerifyService;
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public SocialLoginResponse socialLogin(SocialLoginRequest request, HttpServletResponse response) {
        // 1. Social Login access token 검증
        VerifiedIdentity verifiedIdentity = verifySocialInfo(request);

        // 2. 소셜 로그인 처리
        return handleSocialLogin(verifiedIdentity, response);
    }

    // ==========================================================
    // =  Main Logic - Verify Social Info               =
    // ==========================================================
    public VerifiedIdentity verifySocialInfo(SocialLoginRequest request) {
        // 문자열 - enum 반환
        SocialProvider providerEnum = resolveProvider(request.getProvider());

        // 여기서 실제 서버 검증 수행 (카카오/구글/네이버 중 자동 선택)
        VerifiedIdentity v = socialVerifyService.verify(providerEnum, request.getAccessToken());

        log.info("Verified provider={}, providerUserId={}, email={}", v.provider(), v.providerUserId(), v.email());
        return v;
    }


    // ==========================================================
    // =        Sub Logic - 문자열을 SocialProvider로 변환        =
    // ==========================================================
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
            throw new BusinessException(ErrorCode.PROVIDER_NOT_SUPPORTED);
        }
    }

    public String createRefreshToken(String refreshToken) {

        // 1. refreshToken parsing해서 id 정보 가져오기

        // 2. id정보 기반으로 refreshToken 재생성
//        jwtUtil.generateRefreshToken()

        // 3. 전달

        return null;
    }

    // ==========================================================
    // =  Main Logic - Social Login                 =
    // ==========================================================
    private SocialLoginResponse handleSocialLogin(VerifiedIdentity vi, HttpServletResponse response) {
        Instant now = Instant.now();

        // 1) 사용자 upsert (provider + providerUserId 기준)
        User user = upsertUser(vi, now);

        // 2) 세션 생성
        String sid = generateSessionId();

        // 3) 토큰 발급
        String accessToken = jwtUtil.generateAccessToken(user.getId(), sid);
        String refreshToken = jwtUtil.generateRefreshToken(user.getId(), sid);

        // 4) RefreshToken 저장 (현재 스키마 유지)
        saveRefreshToken(user.getId(), sid, refreshToken, now);

        // 5) 응답 구성
        setResponseTokens(response, accessToken, refreshToken);

        // 민감정보 로그 금지 (token, email 전체 등)
        log.info("social-login success: provider={}, userId={}, sid={}", user.getProvider(), user.getId(), sid);

        return new SocialLoginResponse(user.getProvider(), user.getEmail());
    }


    // ==========================================================
    // = Sub Logic - Social Login : 사용자 생성 / 토큰 발급 등
    // ==========================================================

    // upsert = update + insert
    private User upsertUser(VerifiedIdentity vi, Instant now) {
        User user = userRepository.findUserByProviderUserIdAndProvider(vi.providerUserId(), vi.provider());

        if (user == null) {
            // 새로운 사용자 생성
            user = new User(
                    vi.providerUserId(),
                    vi.provider(),
                    vi.email(),
                    null,
                    now,
                    null
            );
            log.info("New user created: {}", user);
        } else {
            // 이메일이 바뀌었거나 새로 제공되면 갱신
            if (vi.email() != null && !vi.email().equals(user.getEmail())) {
                user.setEmail(vi.email());
            }
            user.setLastLoginAt(now);
            log.info("login user {} ", user);
        }

        return userRepository.save(user);  // 신규/갱신 모두 save로 일원화
    }

    private String generateSessionId() {
        return UUID.randomUUID().toString();
    }

    private void saveRefreshToken(Long userId, String sid, String refreshToken, Instant now) {
        Instant rtExpiresAt = now.plus(jwtUtil.refreshTtlSeconds(), ChronoUnit.SECONDS);
        RefreshToken dbRt = new RefreshToken(userId, sid, refreshToken, rtExpiresAt, now);
        refreshTokenRepository.save(dbRt);
    }

    private void setResponseTokens(HttpServletResponse response, String accessToken, String refreshToken) {
        response.setHeader("Authorization", "Bearer " + accessToken);
        cookieUtil.addRefreshTokenCookie(response, refreshToken);
    }

    // ==========================================================
    // =  Main Logic - Reissue Refresh Token              =
    // ==========================================================
    public boolean reissueRefreshToken(String refreshToken, HttpServletResponse response) {
        Instant now = Instant.now();

        // 1. RT parsing
        Claims rt;
        try {
            rt = jwtUtil.verifyRefresh(refreshToken);
        } catch (ExpiredJwtException e) {
            throw new BusinessException(ErrorCode.REFRESH_TOKEN_EXPIRED);
        } catch (JwtException e) {
            throw new BusinessException(ErrorCode.INVALID_JWT_TOKEN);
        }

        Long userId = Long.valueOf(rt.getSubject());
        String sid = (String) rt.get("sid");

        // 1) DB 조회
        RefreshToken savedToken = refreshTokenRepository.findRefreshTokenBySessionIdAndToken(sid, refreshToken);
        if (savedToken == null) {
            // Handle Exception
            throw new BusinessException(ErrorCode.REFRESH_TOKEN_NOT_FOUND);
        }

        // 2) 새 Access Token 발급
        String newAccessToken = jwtUtil.generateAccessToken(userId, sid);
        response.setHeader("Authorization", "Bearer " + newAccessToken);

        // 3) Refresh Token 만료 임박 여부 확인 후 갱신
        boolean refreshNearExpiry = savedToken.getExpiryAt().isBefore(now.plusSeconds(60 * 60 * 24)); // 1일 이내
        if (refreshNearExpiry) {
            String newRefreshToken = jwtUtil.generateRefreshToken(userId, sid);
            Instant newExpiry = now.plusSeconds(jwtUtil.refreshTtlSeconds());

            // RT 갱신
            savedToken.updateToken(newRefreshToken, newExpiry);
            refreshTokenRepository.save(savedToken);

            cookieUtil.addRefreshTokenCookie(response, newRefreshToken);
        }

        return true;
    }



}
