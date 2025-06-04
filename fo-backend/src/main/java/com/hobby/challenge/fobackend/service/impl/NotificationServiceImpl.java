package com.hobby.challenge.fobackend.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hobby.challenge.fobackend.dto.NotificationResponseDTO;
import com.hobby.challenge.fobackend.dto.ParticipantDTO;
import com.hobby.challenge.fobackend.entity.Notification;
import com.hobby.challenge.fobackend.mapper.NotificationMapper;
import com.hobby.challenge.fobackend.mapper.ParticipationMapper;
import com.hobby.challenge.fobackend.service.NotificationService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final NotificationMapper notificationMapper;
    private final ParticipationMapper participationMapper;

    @Override
    public List<NotificationResponseDTO> getNotifications(Integer userId, int page, int size) {
        int offset = (page - 1) * size;
        return notificationMapper.findByUserId(userId, size, offset);
    }

    @Override
    public int getUnreadCount(Integer userId) {
        return notificationMapper.countUnreadByUserId(userId);
    }

    @Override
    @Transactional
    public void markAsRead(Integer notificationId, Integer userId) {
        notificationMapper.markAsRead(notificationId, userId);
    }

    @Override
    @Transactional
    public void markAllAsRead(Integer userId) {
        notificationMapper.markAllAsRead(userId);
    }

    // 챌린지 참여 요청 알림 (챌린지 생성자에게)
    @Override
    @Transactional
    public void createChallengeRequestNotification(Integer challengeOwnerId, Integer participationId, Integer requesterUserId) {
        Notification notification = Notification.builder()
            .userId(challengeOwnerId)
            .actorUserId(requesterUserId)  // 🆕 요청한 사람
            .participationId(participationId)
            .type("CHALLENGE_REQUEST")
            .isRead(false)
            .build();
        notificationMapper.insertNotification(notification);
    }

    // 참여 승인 알림 (참여자에게)
    @Override
    @Transactional
    public void createParticipationApprovedNotification(Integer participantUserId, Integer challengeId) {
        Notification notification = Notification.builder()
            .userId(participantUserId)
            .challengeId(challengeId)
            .type("CHALLENGE_REQUEST_APPROVED")
            .isRead(false)
            .build();
        notificationMapper.insertNotification(notification);
    }

    // 참여 거절 알림 (참여자에게)
    @Override
    @Transactional
    public void createParticipationRejectedNotification(Integer participantUserId, Integer challengeId) {
        Notification notification = Notification.builder()
            .userId(participantUserId)
            .challengeId(challengeId)
            .type("CHALLENGE_REQUEST_REJECTED")
            .isRead(false)
            .build();
        notificationMapper.insertNotification(notification);
    }

    // 새 인증 알림 (챌린지 참여자들에게)
    @Override
    @Transactional
    public void createNewCertNotification(Integer challengeId, Integer certId) {
        // 이 부분은 챌린지의 모든 참여자에게 알림을 보내야 하므로
        // 추후 구현 (일단 기본 구조만)
        // TODO: 챌린지의 모든 APPROVED 참여자들에게 알림 생성
    }

    // 새 댓글 알림 (인증 작성자에게)
    @Override
    @Transactional  
    public void createNewCommentNotification(Integer certOwnerId, Integer certId, Integer commenterUserId) {
        Notification notification = Notification.builder()
            .userId(certOwnerId)
            .actorUserId(commenterUserId)  // 🆕 댓글 작성자
            .certId(certId)
            .type("NEW_COMMENT")
            .isRead(false)
            .build();
        notificationMapper.insertNotification(notification);
    }

    // 좋아요 알림 (인증 작성자에게)
    @Override
    @Transactional
    public void createNewLikeNotification(Integer certOwnerId, Integer certId, Integer likerUserId) {
        Notification notification = Notification.builder()
            .userId(certOwnerId)
            .actorUserId(likerUserId) // 좋아요 누른 사람
            .certId(certId)
            .type("NEW_LIKE")
            .isRead(false)
            .build();
        notificationMapper.insertNotification(notification);
    }
    
    // 챌린지 시작 예정 알림 (시작 1일 전)
    @Override
    @Transactional
    public void createChallengeStartingSoonNotification(Integer challengeId) {
        // 해당 챌린지의 모든 승인된 참여자에게 알림 발송
        List<ParticipantDTO> participants = participationMapper.findApprovedByChallenge(challengeId);
        
        for (ParticipantDTO participant : participants) {
            Notification notification = Notification.builder()
                .userId(participant.getUserId())
                .challengeId(challengeId)
                .type("CHALLENGE_STARTING_SOON")
                .isRead(false)
                .build();
            notificationMapper.insertNotification(notification);
        }
    }
    
    // 챌린지 시작 알림
    @Override
    @Transactional
    public void createChallengeStartedNotification(Integer challengeId) {
        List<ParticipantDTO> participants = participationMapper.findApprovedByChallenge(challengeId);
        
        for (ParticipantDTO participant : participants) {
            Notification notification = Notification.builder()
                .userId(participant.getUserId())
                .challengeId(challengeId)
                .type("CHALLENGE_STARTED")
                .isRead(false)
                .build();
            notificationMapper.insertNotification(notification);
        }
    }
    
    // 챌린지 종료 예정 알림 (종료 1일 전)
    @Override
    @Transactional
    public void createChallengeEndingSoonNotification(Integer challengeId) {
        List<ParticipantDTO> participants = participationMapper.findApprovedByChallenge(challengeId);
        
        for (ParticipantDTO participant : participants) {
            Notification notification = Notification.builder()
                .userId(participant.getUserId())
                .challengeId(challengeId)
                .type("CHALLENGE_ENDING_SOON")
                .isRead(false)
                .build();
            notificationMapper.insertNotification(notification);
        }
    }
    
    // 챌린지 종료 알림
    @Override
    @Transactional
    public void createChallengeEndedNotification(Integer challengeId) {
        List<ParticipantDTO> participants = participationMapper.findApprovedByChallenge(challengeId);
        
        for (ParticipantDTO participant : participants) {
            Notification notification = Notification.builder()
                .userId(participant.getUserId())
                .challengeId(challengeId)
                .type("CHALLENGE_ENDED")
                .isRead(false)
                .build();
            notificationMapper.insertNotification(notification);
        }
    }
}