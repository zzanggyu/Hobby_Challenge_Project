package com.hobby.challenge.fobackend.entity;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
    private Integer notificationId;
    private Integer userId; // 알림 수신자
    private Integer challengeId; // 챌린지 관련 알림일 때
    private Integer participationId; // 참여 관련 알림일 때
    private Integer certId; // 인증 관련 알림일 때
    private String type; // ENUM 타입
    private Boolean isRead; // 읽음 여부
    private LocalDateTime createdDate;
    private Integer actorUserId; // 알림 발신자
}