package com.moidot.backend.auth.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Component
public class JwtUtil {

    private final SignatureAlgorithm SIGNATURE_ALGORITHM = SignatureAlgorithm.HS256;
    private final SecretKey signingKey;
    private final String issuer;
    private final Duration accessTtl;
    private final Duration refreshTtl;

    // ★ 검증용 파서(재사용)
    private final JwtParser accessParser;
    private final JwtParser refreshParser;

    public JwtUtil(
            @Value("${jwt.secret.key}") String secretKey,
            @Value("${jwt.issuer}") String issuer,
            @Value("${jwt.access-ttl-minutes:15}") long accessTtlMinutes,   // ${propertyName:defaultValue}
            @Value("${jwt.refresh-ttl-days:14}") long refreshTtlDays,
            @Value("${jwt.clockSkewSeconds:60}") long skewSeconds           // ★ 시계 오차 허용(기본 60s)
    ) {
        this.signingKey = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        this.issuer = issuer;
        this.accessTtl = Duration.ofMinutes(accessTtlMinutes);
        this.refreshTtl = Duration.ofDays(refreshTtlDays);

        // ★ Access/Refresh 동일 규약이면 같은 키/issuer로 파서를 준비
        this.accessParser = Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .requireIssuer(issuer)
                .setAllowedClockSkewSeconds(skewSeconds)
                .build();

        this.refreshParser = this.accessParser; // 현재는 동일 정책 사용 (분리 필요시 별도 빌드)
    }

    /* ===== 발급 ===== */
    public String generateAccessToken(String providerUserId) {
        return buildJwt(providerUserId, accessTtl, /*withJti*/ false);
    }

    public String generateRefreshToken(String providerUserId) {
        return buildJwt(providerUserId, refreshTtl, /*withJti*/ true);
    }

    private String buildJwt(String providerUserId, Duration ttl, boolean withJti) {
        Instant now = Instant.now();

        var b = Jwts.builder()
                .setSubject(providerUserId)                               // sub: 토큰 주체(유저 이메일)
                .setIssuer(issuer)                               // iss
                .setIssuedAt(Date.from(now))                     // iat: 발급 시각
                .setExpiration(Date.from(now.plus(ttl)))         // exp
                .signWith(signingKey, SIGNATURE_ALGORITHM);

        if (withJti) b.setId(UUID.randomUUID().toString());             // jti: 토큰 고유 ID(재사용 방지용)

        return b.compact();
    }


    /* ===== 검증 ===== */
    /** Access 토큰 검증(서명/만료/issuer). 유효하면 Claims 반환, 아니면 JwtException */
    public Claims verifyAccess(String jwt) throws JwtException {
        return accessParser.parseClaimsJws(jwt).getBody();
    }

    /** Refresh 토큰 검증(서명/만료/issuer). 유효하면 Claims 반환, 아니면 JwtException */
    public Claims verifyRefresh(String jwt) throws JwtException {
        return refreshParser.parseClaimsJws(jwt).getBody();
    }

    /* ===== 편의 ===== */
    /** Refresh TTL(초) — 쿠키 maxAge 등에 사용 */
    public long refreshTtlSeconds() {
        return refreshTtl.toSeconds();
    }


}

