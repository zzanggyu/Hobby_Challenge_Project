package com.hobby.challenge.fobackend.service;

import com.hobby.challenge.fobackend.dto.LoginRequestDTO;
import com.hobby.challenge.fobackend.dto.LoginResponseDTO;
import com.hobby.challenge.fobackend.dto.SignupRequestDTO;
import com.hobby.challenge.fobackend.dto.SignupResponseDTO;
import com.hobby.challenge.fobackend.dto.UserResponseDTO;

import jakarta.servlet.http.HttpServletResponse;
 
public interface AuthService {

    // 회원가입
	SignupResponseDTO register(SignupRequestDTO dto);

	// 로그인 → 토큰 발급 + 쿠키 세팅 + 로그인 이력 + DTO 반환
    LoginResponseDTO login(LoginRequestDTO dto, HttpServletResponse response);
    
    // 로그아웃 → 쿠키 삭제
    void logout(String refreshToken,HttpServletResponse response);
    
    // 현재 로그인한 사용자의 정보 조회
    UserResponseDTO getCurrentUser(String loginId);
    
    // 리프레시 토큰으로 Access Token 재발급
    void refreshToken(String refreshToken, HttpServletResponse response);
}
