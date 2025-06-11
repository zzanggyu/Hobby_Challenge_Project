package com.hobby.challenge.fobackend.service.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hobby.challenge.fobackend.dto.ChangePasswordDTO;
import com.hobby.challenge.fobackend.dto.UserResponseDTO;
import com.hobby.challenge.fobackend.entity.PointHistory;
import com.hobby.challenge.fobackend.entity.User;
import com.hobby.challenge.fobackend.exception.CustomException;
import com.hobby.challenge.fobackend.exception.ErrorCode;
import com.hobby.challenge.fobackend.mapper.PointHistoryMapper;
import com.hobby.challenge.fobackend.mapper.UserMapper;
import com.hobby.challenge.fobackend.service.NotificationService;
import com.hobby.challenge.fobackend.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final PointHistoryMapper pointHistoryMapper;
    private final NotificationService notificationService;
    
    // 마이페이지: 사용자 정보 조회
    @Override
    public UserResponseDTO getUserInfo(Integer userId) {
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }
        // 엔티티 → DTO 변환
        return UserResponseDTO.builder()
                .userId(user.getUserId())
                .loginId(user.getLoginId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .birthDate(user.getBirthDate())
                .imageUrl(user.getImageUrl())
                .role(user.getRole())
                .points(user.getPoints())
                .level(user.getLevel())
                .createdDate(user.getCreatedDate())
                .build();
    }
    
    // 닉네임 변경
    @Override
    @Transactional
    public UserResponseDTO updateNickname(Integer userId, String nickname) {
        // 닉네임 중복 체크
        if (userMapper.existsByNickname(nickname, userId)) {
            throw new CustomException(ErrorCode.DUPLICATE_NICKNAME, 
                "이미 사용 중인 닉네임입니다.");
        }
        
        userMapper.updateNickname(userId, nickname); 
        return getUserInfo(userId); // 변경 후 정보 반환
    }
    
    // 레벨 계산
    @Override
    public int calculateLevel(int points) {
        int level = 1;
        int totalRequiredPoints = 0;
        
        // 레벨업 필요 포인트: 1레벨 = 10, 2레벨 = 30, 3레벨 = 60, 4레벨 = 100...
        for (int additionalPoints = 10; additionalPoints <= 500; additionalPoints += 10) {
            totalRequiredPoints += additionalPoints;
            
            if (points >= totalRequiredPoints) {
                level++;
            } else {
                break;
            }
        }
        
        return level;
    }
    
    // 포인트 추가 및 레벨 업데이트
    @Override
    @Transactional
    public void addPoints(Integer userId, int pointsToAdd, String reason) {
        // 현재 사용자 정보 조회
        User user = userMapper.findById(userId);
        int oldLevel = user.getLevel();
        int oldPoints = user.getPoints();
        
        // 포인트 추가
        int newPoints = user.getPoints() + pointsToAdd;
        int newLevel = calculateLevel(newPoints);
        

        
        // DB 업데이트
        userMapper.updatePointsAndLevel(userId, newPoints, newLevel);
        
        // 포인트 히스토리 기록
        PointHistory history = PointHistory.builder()
            .userId(userId)
            .type(reason) // "CERT_UPLOAD", "CHALLENGE_SUCCESS" 등
            .points(pointsToAdd)
            .build();
        pointHistoryMapper.insert(history);
        System.out.println("포인트 히스토리 기록");
        
        // 레벨업 확인
        if (newLevel > oldLevel) {
            System.out.println("레벨업 " + oldLevel + " → " + newLevel);
        } else {
            System.out.println("레벨 변화 없음 (현재 레벨: " + newLevel + ")");
        }

//        // 레벨업 알림
//        if (newLevel > oldLevel) {
//            notificationService.createLevelUpNotification(userId, newLevel);
//        }
    }
    
    // 비밀번호 변경
    @Override
    @Transactional
    public void changePassword(Integer userId, ChangePasswordDTO dto) {
        User user = userMapper.findById(userId);
        
        // 현재 비밀번호 확인
        if (!passwordEncoder.matches(dto.getCurrentPassword(), user.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD, 
                "현재 비밀번호가 일치하지 않습니다.");
        }
        
        // 새 비밀번호로 변경
        String encodedPassword = passwordEncoder.encode(dto.getNewPassword());
        userMapper.updatePassword(userId, encodedPassword);
    }
    
    // 회원 탈퇴 (상태값 변경) 
    @Override
    @Transactional
    public void deleteAccount(Integer userId, String password) {
        User user = userMapper.findById(userId);
        
        // 비밀번호 확인
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD, 
                "비밀번호가 일치하지 않습니다.");
        }
        
        // 회원 탈퇴
        userMapper.deleteUser(userId);
    }
}
