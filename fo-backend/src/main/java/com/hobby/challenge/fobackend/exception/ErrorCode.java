package com.hobby.challenge.fobackend.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
	// 상수 정의: (code, 기본 메세지)
	// HTTP code + 도메인 번호 (User 01, Challenge 02, ...) + 생성 번호
    // 01: User 도메인
    DUPLICATE_LOGINID ("400011", "이미 존재하는 아이디입니다."),
    DUPLICATE_EMAIL ("400012", "이미 가입된 이메일입니다."), 
    USER_NOT_FOUND ("404011", "해당 사용자를 찾을 수 없습니다."),
    INVALID_CREDENTIALS ("400013", "비밀번호가 틀렸습니다."),
    INVALID_BIRTHDATE ("400014", "생년월일은 오늘 이전 날짜부터 입력할 수 있습니다."),
    INVALID_VERIFICATION_CODE ("400017", "인증코드가 틀렸습니다."),
    
    // 리프레시 토큰 관련
    INVALID_REFRESH_TOKEN ("400015", "리프레시 토큰이 만료되었거나 유효하지 않습니다. 다시 로그인해주세요."),
    REFRESH_TOKEN_MISSING ("400016", "리프레시 토큰이 제공되지 않았습니다."),
    
    // 이 곳에 새 필드 추가	
    
    // 02: Challenge 도메인
    INVALID_CHALLENGE ("400021", "챌린지 입력값이 올바르지 않습니다."),
    CHALLENGE_NOT_FOUND ("404021", "해당 챌린지를 찾을 수 없습니다."),
    PARTICIPATION_NOT_APPROVED ("404022", "인증을 등록하려면 먼저 챌린지 참여 승인을 받아야 합니다."),
    CERTIFICATION_ACCESS_DENIED ("404023", "인증은 승인된 참여자만 가능합니다."),
    CHALLENGE_UPDATE_FORBIDDEN ("403024","챌린지 생성자만 수정 가능합니다"),
    CHALLENGE_DELETE_FORBIDDEN ("403025","챌린지 생성자만 삭제 가능합니다."),
    // 이 곳에 새 필드 추가
    
    // 03: Category 도메인
    CATEGORY_NOT_FOUND ("404031", "해당 카테고리를 찾을 수 없습니다."),
    // 이 곳에 새 필드 추가
    
    // 04: Certification 도메인
    DUPLICATE_CERTIFICATION ("400041", "오늘은 이미 인증을 등록했습니다."),
    CERTIFICATION_INVALID_PERIOD ("400042", "챌린지 기간 내에만 인증할 수 있습니다."),
    CERTIFICATION_DELETE_FORBIDDEN ("400043", "본인의 인증만 삭제할 수 있습니다."),
    CERTIFICATION_UPDATE_FORBIDDEN("400044", "본인의 인증만 수정할 수 있습니다."),
    NOT_FOUND_CERTIFICATION("404045", "인증을 찾을 수 없습니다."),
    CHALLENGE_LIMIT_EXCEEDED("400026", "챌린지는 하나만 생성할 수 있습니다."),
    
    // 05: CertComment
    COMMENT_UPDATE_FORBIDDEN ("404051", "본인의 댓글만 수정할 수 있습니다."),
    COMMENT_DELETE_FORBIDDEN ("404051", "본인의 댓글만 삭제할 수 있습니다."),
    
    
    // 06: Participation
    PARTICIPATION_CANCEL_FORBIDDEN ("404061", "권한이 없거나 이미 취소된 요청입니다."),
    PARTICIPATION_LIMIT_EXCEEDED("404062","이미 다른 챌린지에 참여 요청 또는 참여 중입니다.\n먼저 기존 요청을 취소하거나 챌린지에서 탈퇴해주세요."),
    
    // 00: 기타/공통/서버취소 		        

    INTERNAL_SERVER_ERROR("500001", "서버 오류가 발생했습니다.");
	// 이 곳에 새 필드 추가
	
	
	private final String code; 
	private final String message;
}
