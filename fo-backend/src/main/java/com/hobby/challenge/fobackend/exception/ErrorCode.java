package com.hobby.challenge.fobackend.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
	// 상수 정의: (code, 기본 메세지)
	// HTTP code + 도메인 번호 (User 01, Challenge 02, ...) + 생성 번호
    // 01: User 도메인
    DUPLICATE_LOGINID    ("400011", "이미 존재하는 아이디입니다."),
    DUPLICATE_EMAIL      ("400012", "이미 가입된 이메일입니다."), 
    USER_NOT_FOUND       ("404011", "해당 사용자를 찾을 수 없습니다."),
    // 이 곳에 새 필드 추가	
    
    // 02: Challenge 도메인
    INVALID_CHALLENGE    ("400021", "챌린지 입력값이 올바르지 않습니다."),
    CHALLENGE_NOT_FOUND  ("404021", "해당 챌린지를 찾을 수 없습니다."),
    // 이 곳에 새 필드 추가
    
    // 03: Category 도메인
    CATEGORY_NOT_FOUND   ("404031", "해당 카테고리를 찾을 수 없습니다."),
    // 이 곳에 새 필드 추가
    
    // 00: 기타/공통/서버
    INTERNAL_SERVER_ERROR("500001", "서버 오류가 발생했습니다.");
	// 이 곳에 새 필드 추가
	
	
	private final String code; 
	private final String message;
}
