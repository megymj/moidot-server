package com.moidot.backend.auth.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Component
public class JwtUtil {

    @Value("${jwt.secret.key}")
    private String SECRET_KEY;
    private final SignatureAlgorithm SIGNATURE_ALGORITHM = SignatureAlgorithm.HS256;

    private final long ACCESS_TOKEN_EXPIRATION = 1000 * 60 * 60; // 60분
    private final long REFRESH_TOKEN_EXPIRATION = 1000L * 60 * 60 * 24 * 14; // 14일

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes()); // 최소 256bit(32바이트) 필요
    }

    public String generateAccessToken(String email) {
        return generateToken(email, ACCESS_TOKEN_EXPIRATION);
    }

    public String generateRefreshToken(String email) {
        return generateToken(email, REFRESH_TOKEN_EXPIRATION);
    }

    private String generateToken(String email, long expiration) {
        Date now = new Date();

        return Jwts.builder()
                .setSubject(email)                  // sub: 토큰 주체(유저 이메일)
                .setAudience("web")                  // aud: 토큰 사용 대상(여기서는 'web')
                .setIssuedAt(now)                   // iat: 발급 시각
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .setId(UUID.randomUUID().toString()) // jti: 토큰 고유 ID(재사용 방지용)
                .signWith(getSigningKey(), SIGNATURE_ALGORITHM)
                .compact();
    }
}

