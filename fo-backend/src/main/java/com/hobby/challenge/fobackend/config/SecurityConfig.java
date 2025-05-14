package com.hobby.challenge.fobackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
          .csrf(csrf -> csrf.disable())
          .authorizeHttpRequests(auth -> auth
            // 회원가입·로그인 관련 엔드포인트는 모두 허용
            .requestMatchers("/api/auth/**").permitAll()
            // 그 외 모든 요청은 인증 필요
            .anyRequest().authenticated()
          )
          // formLogin, httpBasic 등 원하는 인증 방식을 추가
          .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}

