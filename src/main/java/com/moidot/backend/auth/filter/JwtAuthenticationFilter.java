package com.moidot.backend.auth.filter;

import com.moidot.backend.auth.util.JwtAccessTokenVerifier;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtAccessTokenVerifier jwtAccessTokenVerifier;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest req) {
        // 프리플라이트(OPTIONS) 우회 + 공개 경로 제외
        String uri = req.getRequestURI();
        boolean preflight = "OPTIONS".equalsIgnoreCase(req.getMethod())
                && req.getHeader("Origin") != null
                && req.getHeader("Access-Control-Request-Method") != null;

        return preflight
                || uri.startsWith("/api/auth/")
                || uri.startsWith("/actuator/")
                || uri.startsWith("/docs/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // JWT 인증 로직 구현 (예: 토큰 검증, 사용자 정보 추출 등)

//        response.setCharacterEncoding("UTF-8");

        log.info(">> Enter JwtAuthenticationFilter {}", request.getRequestURI());

        String auth = request.getHeader("Authorization");
        if (auth == null || !auth.startsWith("Bearer ")) {
            write401(response, 4010, "AUTH_TOKEN_MISSING", "Authorization header missing");
            return;
        }

        String token = auth.substring(7).trim();
        try {
            var claims = jwtAccessTokenVerifier.verify(token); // 서명/exp/iss/aud 검증

            log.info("claims : {}", claims.toString());

            // 컨트롤러에서 쓰도록 인증 컨텍스트 저장
//            request.setAttribute("auth.ctx",
//                    new AuthContext(claims.getSubject(), claims.get("email", String.class)));

            filterChain.doFilter(request, response);
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            write401(response, 4011, "AUTH_TOKEN_EXPIRED", "Access token has expired");
        } catch (io.jsonwebtoken.JwtException e) { // 서명 불일치/형식 오류 등
            write401(response, 4012, "AUTH_TOKEN_INVALID", "Access token invalid");
        }
    }


    private void write401(HttpServletResponse res, int code, String codeName, String detail) throws IOException {
        res.setStatus(401);
        res.setHeader("WWW-Authenticate", "Bearer error=\"invalid_token\"");
        res.setContentType("application/problem+json; charset=UTF-8");
        String body = """
            {
              "timestamp":"%s",
              "status":401,
              "code":%d,
              "codeName":"%s",
              "title":"Unauthorized",
              "detail":"%s"
            }
            """.formatted(java.time.Instant.now(), code, codeName, detail);
        res.getWriter().write(body);
    }

    public record AuthContext(String userId, String email) {}
}
