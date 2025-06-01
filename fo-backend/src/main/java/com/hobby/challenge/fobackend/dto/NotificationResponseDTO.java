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
    private String title;        // 동적으로 생성할 제목
    private String message;      // 동적으로 생성할 메시지
    private String targetType;   // challenge, certification 등
    private Integer targetId;    // 관련된 ID
    private Boolean read;
    private LocalDateTime createdAt;
    
    // 추가 정보 (조인해서 가져올 수 있는 정보)
    private String challengeTitle;  // 챌린지 제목
    private String participantNickname; // 참여자 닉네임
}
