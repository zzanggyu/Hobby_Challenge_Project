package com.hobby.challenge.fobackend.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

// 모든 요청에 대해 JWT를 학인 후 유효하면 SecurityContext에 인증 정보를 설정
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter { // 모든 HTTP 요청마다 doFilterInternal 한 번씩 호충
	private final JwtTokenProvider tokenProvider;
	private final CustomUserDetailsService userDetailService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, 
			HttpServletResponse response, 
			FilterChain filterChain)
			throws ServletException, IOException {
		String header = request.getHeader("Authorization"); // Authorization 헤더에서 토큰 추출
		if (header != null && header.startsWith("Bearer ")) { 
			String token = header.substring(7); // "Bearer " 제외한 순수 토큰 
			
			if (tokenProvider.validateToken(token)) { // 토큰 유효 체크
				String loginId = tokenProvider.getLoginId(token); // 페이로드(sub)에 담긴 loginId를 꺼냄
				var userDetails = userDetailService.loadUserByUsername(loginId); // DB에서 사용자 정보(UserDetails) 로드
				var auth = new UsernamePasswordAuthenticationToken( // 인증 객체 생성
						userDetails,
						null,
						userDetails.getAuthorities() // 권한 정보
						);
				auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request)); // 인증 객체에 요청 세부 정보(IP, session 등) 설정
				SecurityContextHolder.getContext().setAuthentication(auth); // SecurityContext에 최종 인증 정보 저장. 이 시점부터 사용자는 인증된 상태이다.
			}
		}
		filterChain.doFilter(request, response); // 다음 필터로 요청/응답 전달

	}

}
