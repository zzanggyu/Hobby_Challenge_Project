package com.hobby.challenge.fobackend.service;

import java.util.List;
import com.hobby.challenge.fobackend.dto.NotificationResponseDTO;

public interface NotificationService {
    // 알림 목록 조회 (페이징)
    List<NotificationResponseDTO> getNotifications(Integer userId, int page, int size);
    
    // 읽지 않은 알림 개수
    int getUnreadCount(Integer userId);
    
    // 알림 읽음 처리
    void markAsRead(Integer notificationId, Integer userId);
    
    // 모든 알림 읽음 처리
    void markAllAsRead(Integer userId);
    
    // 새 알림 생성 메서드들
    void createChallengeRequestNotification(Integer challengeOwnerId, Integer participationId);
    void createParticipationApprovedNotification(Integer participantUserId, Integer challengeId);
    void createParticipationRejectedNotification(Integer participantUserId, Integer challengeId);
    void createNewCertNotification(Integer challengeId, Integer certId);
    void createNewCommentNotification(Integer certOwnerId, Integer certId);
    void createNewLikeNotification(Integer certOwnerId, Integer certId);
}
