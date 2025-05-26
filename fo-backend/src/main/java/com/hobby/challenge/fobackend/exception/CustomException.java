package com.hobby.challenge.fobackend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import lombok.Getter;

@ResponseStatus(HttpStatus.FORBIDDEN)
@Getter
public class CustomException extends RuntimeException{ // 일반적으로 사용하는 예외는 거의 Runtime에 발생하므로 RuntimeException 상속
	private final ErrorCode errorCode; // 코드,메세지 정의해둔 enum
	private final String detail; // 내부용 로깅 메세지 추가 디버깅
	
    // 1) 기본 메시지만 사용하고 싶을 때 
    public CustomException(ErrorCode errorCode) {
        super(errorCode.getMessage()); // super()는 반드시 첫 줄에
        this.errorCode = errorCode;
        this.detail    = null;         // detail도 초기화
    }
	

    // 2) 상세 메시지를 추가하고 싶을 때
    public CustomException(ErrorCode errorCode, String detail) {
        super(detail);
        this.errorCode = errorCode;
        this.detail    = detail;
    }
}
