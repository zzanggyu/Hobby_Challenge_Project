package com.hobby.challenge.fobackend.config;

import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.hobby.challenge.fobackend.security.JwtAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
	private final JwtAuthenticationFilter jwtFilter;

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
        // CORS 설정 활성화
        .cors(withDefaults())
          .csrf(csrf -> csrf.disable()) // REST API이므로 토큰 기반 인증이므로 CSRF 끔
          .sessionManagement(session -> 
          	session.sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 세션 사용 안함 무상태(stateless)정책 설정 JWT로 인증 처리
          )
          .authorizeHttpRequests(auth -> auth // URL별 접근 권한 설정
        	 //  OPTIONS 프리플라이트 요청은 인증 없이 통과시킴
    		.requestMatchers(HttpMethod.OPTIONS, "/api/**").permitAll()
    		 // 회원가입·로그인 관련 엔드포인트는 모두 허용
            .requestMatchers("/api/auth/**").permitAll() // 로그인 리프레시 로그아웃 모두 허용
            .requestMatchers("/api/challenges/popular").permitAll()
            .requestMatchers("/api/categories").permitAll()
            .requestMatchers("/api/rankings/**").permitAll()
            
            // 로그인 되어야 하는 
            .requestMatchers(HttpMethod.GET, "/api/challenges").authenticated()
            .requestMatchers(HttpMethod.POST, "/api/challenges/**").authenticated()
            
            // 나머지 엔드포인트도 필요에 따라
            // 그 외 모든 요청은 JWT 인증을 받은 사용자만 접근 가능
            .anyRequest().authenticated()
            )
          // JWT 인증 필터(요청 헤더의 JWT 검증 유효하면 SecurityContext에 인증 정보 세팅)
          .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class); 
        
          // formLogin, httpBasic 등 원하는 인증 방식을 추가
//          .httpBasic(); // 디버깅용으로 켜둬도 됨

        return http.build();
    }
    
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authConfig
    ) throws Exception {
        return authConfig.getAuthenticationManager(); // Spring이 자동으로 만들어 둔 AuthenticationManager를 반환
    }
}

