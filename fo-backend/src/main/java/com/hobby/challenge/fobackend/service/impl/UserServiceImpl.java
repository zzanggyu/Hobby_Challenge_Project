package com.hobby.challenge.fobackend.service.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hobby.challenge.fobackend.dto.ChangePasswordDTO;
import com.hobby.challenge.fobackend.dto.UserResponseDTO;
import com.hobby.challenge.fobackend.entity.User;
import com.hobby.challenge.fobackend.exception.CustomException;
import com.hobby.challenge.fobackend.exception.ErrorCode;
import com.hobby.challenge.fobackend.mapper.UserMapper;
import com.hobby.challenge.fobackend.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    
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
