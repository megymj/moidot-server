package com.moidot.backend.auth.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
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

    public JwtUtil(
            @Value("${jwt.secret.key}") String secretKey,
            @Value("${jwt.issuer}") String issuer,
            @Value("${jwt.access-ttl-minutes:15}") long accessTtlMinutes,   // ${propertyName:defaultValue}
            @Value("${jwt.refresh-ttl-days:14}") long refreshTtlDays
    ) {
        this.signingKey = Keys.hmacShaKeyFor(secretKey.getBytes());
        this.issuer = issuer;
        this.accessTtl = Duration.ofMinutes(accessTtlMinutes);
        this.refreshTtl = Duration.ofDays(refreshTtlDays);
    }


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
}

