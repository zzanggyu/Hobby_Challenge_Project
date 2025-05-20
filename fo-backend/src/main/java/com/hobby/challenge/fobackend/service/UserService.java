package com.hobby.challenge.fobackend.service;

import com.hobby.challenge.fobackend.dto.SignupRequestDTO;
import com.hobby.challenge.fobackend.entity.User;
 
public interface UserService {

    User getUserById(Integer id); // 사용자 식별번호로 사용자 조회
    User getUserByLoginId(String loginId); // 로그인 아이디로 사용자 조회
    User register(SignupRequestDTO sr); // 회원가입
    User authenticate(String loginId, String rawPassword); // 로그인 인증 
    
}
