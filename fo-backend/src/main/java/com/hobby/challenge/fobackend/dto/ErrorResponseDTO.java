package com.hobby.challenge.fobackend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter // 에러 응답 DTO는 응답으로만 나가기 때문에 @Data 안씀
@AllArgsConstructor // 모든 필드를 파라미터로 받는 생성자 생성
public class ErrorResponseDTO {
    private final String errorCode; // ex) 4001 에러 식별코드
    private final String message; // 사용자에게 보여줄 설명
    private final String detail; // 추가 디버깅 정보 내부 로깅용 메세지
    
}
