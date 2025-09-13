package com.moidot.backend.sample;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Map;

@Slf4j
@RequestMapping("/api/sample")
@RestController
public class SampleController {

//    @GetMapping
//    public Object hello() {
//        log.info("Sample Test API Called");
//        return "Sample Test API Success!";
//    }

    @GetMapping
    public Map<String, Object> hello(
            @CookieValue(value = "m_refreshToken", required = false) String refreshToken,
            HttpServletRequest request
    ) {

        log.info("m_refreshToken = {}", refreshToken);

        boolean present = refreshToken != null && !refreshToken.isBlank();
        String masked = present ? mask(refreshToken) : null;

        log.info("Sample Test API Called. refreshToken {}",
                present ? "present: " + masked : "missing");

        // 디버그: 요청에 포함된 모든 쿠키 이름/길이 출력
        if (request.getCookies() != null) {
            Arrays.stream(request.getCookies())
                    .forEach(c -> log.debug("Cookie name={}, len={}",
                            c.getName(),
                            c.getValue() == null ? 0 : c.getValue().length()));
        }

        return Map.of(
                "message", "Sample Test API Success!",
                "refreshTokenPresent", present,
                "refreshTokenMasked", masked
        );
    }

    private String mask(String s) {
        int show = Math.min(6, s.length());
        return s.substring(0, show) + "... (" + s.length() + " chars)";
    }

}
