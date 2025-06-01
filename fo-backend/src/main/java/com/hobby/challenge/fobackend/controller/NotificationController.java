package com.hobby.challenge.fobackend.controller;

import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.hobby.challenge.fobackend.dto.NotificationResponseDTO;
import com.hobby.challenge.fobackend.service.NotificationService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    // 알림 목록 조회 (페이징)
    @GetMapping
    public ResponseEntity<Map<String, Object>> getNotifications(
            @AuthenticationPrincipal(expression="userId") Integer userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        List<NotificationResponseDTO> notifications = 
            notificationService.getNotifications(userId, page, size);
        int unreadCount = notificationService.getUnreadCount(userId);
        
        return ResponseEntity.ok(Map.of(
            "items", notifications,
            "unreadCount", unreadCount
        ));
    }

    // 알림 읽음 처리
    @PatchMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(
            @PathVariable Integer id,
            @AuthenticationPrincipal(expression="userId") Integer userId) {
        
        notificationService.markAsRead(id, userId);
        return ResponseEntity.ok().build();
    }

    // 모든 알림 읽음 처리
    @PatchMapping("/read-all")
    public ResponseEntity<Void> markAllAsRead(
            @AuthenticationPrincipal(expression="userId") Integer userId) {
        
        notificationService.markAllAsRead(userId);
        return ResponseEntity.ok().build();
    }
}
