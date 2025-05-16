package com.hobby.challenge.fobackend.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
	// 상수 정의: (code, 기본 메세지)
	DUPLICATE_LOGINID ("4001", "이미 존재하는 아이디입니다."),
	USER_NOT_FOUND ("4040", "해당 사용자를 찾을 수 없습니다."),
	DUPLICATE_EMAIL  ("4002", "이미 가입된 이메일입니다."),
	;
	// 이 곳에 새 필드 추가
	
	
	private final String code; 
	private final String message;
}
