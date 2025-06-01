package com.hobby.challenge.fobackend.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.hobby.challenge.fobackend.dto.NotificationResponseDTO;
import com.hobby.challenge.fobackend.entity.Notification;

@Mapper
public interface NotificationMapper {
    // 알림 목록 조회 (페이징)
    List<NotificationResponseDTO> findByUserId(@Param("userId") Integer userId, 
                                             @Param("size") int size, 
                                             @Param("offset") int offset);
    
    // 읽지 않은 알림 개수
    int countUnreadByUserId(@Param("userId") Integer userId);
    
    // 알림 읽음 처리
    void markAsRead(@Param("notificationId") Integer notificationId, 
                    @Param("userId") Integer userId);
    
    // 모든 알림 읽음 처리
    void markAllAsRead(@Param("userId") Integer userId);
    
    // 새 알림 생성
    void insertNotification(Notification notification);
}