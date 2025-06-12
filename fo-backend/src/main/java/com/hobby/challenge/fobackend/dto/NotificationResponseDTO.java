package com.hobby.challenge.fobackend.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponseDTO {
    private Integer id;
    private Integer userId;
    private String type;
    private String title; // 동적으로 생성할 제목
    private String message; // 동적으로 생성할 메시지
    private String targetType; // challenge, certification 등
    private Integer targetId; // 관련된 ID
    private Boolean read;
    private LocalDateTime createdAt;
    
    // 챌린지/인증 ID 정보
    private Integer challengeId;     // 직접 챌린지 관련 알림의 경우
    private Integer certId;          // 인증 관련 알림의 경우
    private Integer certChallengeId; // 인증이 속한 챌린지 ID
    
    private String actorNickname; // 발송자 닉네임
    private String actorImageUrl; // 발송자 프로필 이미지
    // 추가 정보 (조인해서 가져올 수 있는 정보)
    private String challengeTitle;  // 챌린지 제목
    private String participantNickname; // 참여자 닉네임
}
