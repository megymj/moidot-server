package com.moidot.backend.global.config;

import com.moidot.backend.auth.filter.JwtAuthenticationFilter;
import com.moidot.backend.auth.util.JwtUtil;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    // 1) 필터 자체를 빈으로 등록
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(JwtUtil jwtUtil) {
        return new JwtAuthenticationFilter(jwtUtil);
    }

    // 2) 등록기 빈을 만들 때, 위 필터 빈을 "파라미터로" 자동 주입
    @Bean
    public FilterRegistrationBean<JwtAuthenticationFilter> jwtFilterRegistration(JwtAuthenticationFilter filter) {
        var bean = new FilterRegistrationBean<>(filter); // ← 여기서 실제 필터를 연결

//        bean.addUrlPatterns("/api/*");
        bean.setOrder(1);
        return bean;
    }
}
