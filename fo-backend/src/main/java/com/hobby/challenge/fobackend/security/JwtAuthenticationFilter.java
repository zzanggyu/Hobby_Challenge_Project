package com.hobby.challenge.fobackend.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
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
		
		String path = request.getRequestURI();
	    if (path.equals("/api/auth/refresh") || path.equals("/api/auth/logout")) {
	        filterChain.doFilter(request, response);
	        return;
	    }
		
	 // HTTP 요청에 담겨 온 쿠키들 중 "token" 쿠키를 찾아 값을 저장
	    String token = null; // JWT를 담을 token을 null로 초기화
	    Cookie[] cookies = request.getCookies(); // 쿠키 배열을 변수에 저장
	    System.out.println("🔍 Cookies: " + (cookies != null ? cookies.length : "null")); // 디버깅용

	    if(cookies != null && cookies.length > 0) { // null 체크 + 길이 체크
	        for (Cookie cookie : cookies) {
	            if ("token".equals(cookie.getName())) {
	                token = cookie.getValue(); // "token"인 쿠키 JWT 문자열이  token에 저장됨 
	                break;
	            }
	        }
	    }
	    System.out.println("🔍 Token found: " + (token != null)); // 디버깅용
	    
		// 토큰 유효 시 스프링 시큐리티 컨텍스트에 인증 정보 세팅하고 항상 다음 필터로 요청을 넘긴다.
		if (token != null && tokenProvider.validateToken(token)) { // 토큰 존재 여부와 서명만료 검사
			String loginId = tokenProvider.getLoginId(token); // payload(claim) 중 sub클레임인 loginId를 추출
			var userDetails = userDetailService.loadUserByUsername(loginId); // loginId로 사용자 정보를 UserDetails(비밀번호,활성 여부,권한)로 가져옴
			var auth = new UsernamePasswordAuthenticationToken( // 인증 객체 생성
					userDetails, 
					null,
					userDetails.getAuthorities()
			);
			auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request)); // 인증 객체에 요청 세부 정보(IP, session 등) 설정
			SecurityContextHolder.getContext().setAuthentication(auth); // SecurityContext에 최종 인증 정보 저장. 이 시점부터 사용자는 인증된 상태이다.
		}
		
		filterChain.doFilter(request, response); // 다음 필터로 요청/응답 전달
		
		// HTTP 요청 헤더에서 토큰 꺼냄
//		String header = request.getHeader("Authorization"); // Authorization 헤더에서 토큰 추출
//		if (header != null && header.startsWith("Bearer ")) { 
//			String token = header.substring(7); // "Bearer " 제외한 순수 토큰 
//			
//			if (tokenProvider.validateToken(token)) { // 토큰 유효 체크
//				String loginId = tokenProvider.getLoginId(token); // 페이로드(sub)에 담긴 loginId를 꺼냄
//				var userDetails = userDetailService.loadUserByUsername(loginId); // DB에서 사용자 정보(UserDetails) 로드
//				var auth = new UsernamePasswordAuthenticationToken( // 인증 객체 생성
//						userDetails,
//						null,
//						userDetails.getAuthorities() // 권한 정보
//						);
//				auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request)); // 인증 객체에 요청 세부 정보(IP, session 등) 설정
//				SecurityContextHolder.getContext().setAuthentication(auth); // SecurityContext에 최종 인증 정보 저장. 이 시점부터 사용자는 인증된 상태이다.
//			}
//		}
//		filterChain.doFilter(request, response); // 다음 필터로 요청/응답 전달

	}

}
