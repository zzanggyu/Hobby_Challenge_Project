package com.hobby.challenge.fobackend.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.hobby.challenge.fobackend.entity.User;

@Mapper
public interface UserMapper {
    // userId로 유저 조회 (마이페이지 기본 정보)
    User findById(@Param("userId") Integer userId);
    
    // 닉네임 중복 체크 (변경 시 본인 제외)
    boolean existsByNickname(@Param("nickname") String nickname, 
                             @Param("excludeUserId") Integer excludeUserId);
    
    // 닉네임 업데이트
    void updateNickname(@Param("userId") Integer userId, 
                        @Param("nickname") String nickname);
    
    // 비밀번호 업데이트
    void updatePassword(@Param("userId") Integer userId, 
                        @Param("password") String password);
    
    // 계정 탈퇴 
    void deleteUser(@Param("userId") Integer userId);
}