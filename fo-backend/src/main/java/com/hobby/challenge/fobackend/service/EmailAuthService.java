package com.hobby.challenge.fobackend.service;

public interface EmailAuthService {
	
	// purpose는 signup과 passwordreset 두 가지
	public void sendVerificationCode(String purpose, String email); // 인증 코드 이메일로 전송
	
	public boolean verifyCode(String purpose, String email, String inputCode); // 사용자가 입력한 코드 검증
}
