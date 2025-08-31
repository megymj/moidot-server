package com.moidot.backend.auth.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

@Component
public class JwtAccessTokenVerifier {

    private final JwtParser parser;

    public JwtAccessTokenVerifier(
            @Value("${jwt.secret.key}") String secretKey,
            @Value("${jwt.issuer}") String issuer,
            @Value("${jwt.clockSkewSeconds:60}") long skewSeconds
    ) {
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes());
        this.parser = Jwts.parserBuilder()
                .setSigningKey(key)
                .requireIssuer(issuer)       // iss 일치
                .setAllowedClockSkewSeconds(skewSeconds)
                .build();
    }

    /** 유효하면 Claims 반환, 아니면 JwtException 던짐 */
    public Claims verify(String jwt) throws JwtException {
        return parser.parseClaimsJws(jwt).getBody();
    }

}
