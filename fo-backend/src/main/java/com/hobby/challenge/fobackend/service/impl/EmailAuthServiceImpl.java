package com.hobby.challenge.fobackend.service.impl;

import java.time.Duration;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.hobby.challenge.fobackend.service.EmailAuthService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailAuthServiceImpl implements EmailAuthService{
	private static final long TOKEN_EXPIRE_SECONDS = 300 ; // 이메일 인증 토큰 5분 설정
	private final StringRedisTemplate redis;
	private final JavaMailSender mailSender; // 수정
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	// 이메일로 인증 코드 발송
	public void sendVerificationCode(String email) {
		String key = "email:" + email;
		// 6자리 랜덤 코드 발송
		String code = UUID.randomUUID().toString().substring(0,6);
		// Redis에 키 = eamil, 값은 6자리 코드, TTL=5분으로 저장
		redis.opsForValue().set(email, code, Duration.ofSeconds(TOKEN_EXPIRE_SECONDS));
		logger.debug("Redis에 저장된 인증 코드: key={} value={}", key, code); // 레디스 저장 확인용 로그 출력
		
		// 메일 전송
		SimpleMailMessage msg = new SimpleMailMessage();
		msg.setTo(email);
		msg.setSubject("회원가입 이메일 인증 코드");
		msg.setText("인증 코드: " + code + "\n(유효시간: 5분)");
		mailSender.send(msg);
	}
	
	// 사용자가 입력한 코드 확인
	public boolean verifyCode(String email, String inputCode) {
		String saved = redis.opsForValue().get(email);
		if(saved == null)  return false; // 만료 혹은 발송 실패
		if(!saved.equals(inputCode)) return false;
		return true;
	}
	
	
}
