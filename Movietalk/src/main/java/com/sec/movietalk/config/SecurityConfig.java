package com.sec.movietalk.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    // Spring Security의 보안 필터 체인을 구성하는 메서드
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll() // 모든 요청에 대해 인증 없이 허용해주겠다는 의미 (개발중이라 일단 이렇게 해놨습니다.)
                )
                .csrf(csrf -> csrf.disable()); // CSRF 비활성화도 같이 꺼놨습니다.

        return http.build();
    }
}
