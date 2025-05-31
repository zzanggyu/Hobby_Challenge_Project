package com.hobby.challenge.fobackend.service;

import com.hobby.challenge.fobackend.dto.ChangePasswordDTO;
import com.hobby.challenge.fobackend.dto.UserResponseDTO;

public interface UserService {
    // 유저 기본 정보 조회
    UserResponseDTO getUserInfo(Integer userId);

    // 닉네임 변경
    UserResponseDTO updateNickname(Integer userId, String nickname);

    // 비밀번호 변경
    void changePassword(Integer userId, ChangePasswordDTO dto);

    // 회원 탈퇴 
    void deleteAccount(Integer userId, String password);
}
