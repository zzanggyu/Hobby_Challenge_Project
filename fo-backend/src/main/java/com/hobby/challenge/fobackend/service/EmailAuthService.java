package com.hobby.challenge.fobackend.service;

public interface EmailAuthService {
	
	public void sendVerificationCode(String email); // 인증 코드 이메일로 전송
	public boolean verifyCode(String email, String inputCode); // 사용자가 입력한 코드 검증
}
