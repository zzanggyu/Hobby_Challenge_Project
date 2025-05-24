package com.hobby.challenge.fobackend.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.hobby.challenge.fobackend.entity.User;

@Mapper
public interface AuthMapper {
	// 로그인 아이디로 조회 로그인 시 정보 가져오기
	// SELECT * FROM USER WHERE LOGIN_ID = #{loginId}
	User findByLoginId(@Param("loginId") String loginId);
	
	// 이메일 중복 체크
	User findByEmail(@Param("email") String email);
	
	// 회원 등록 회원가입
	// INSERT INTO USER (LOGIN_ID, USERNAME, PASSWORD, EMAIL, NICKNAME, ROLE)
	// VALUES (#{loginId}, #{username}, #{password}, #{email}, #{nickname}, #{role})
	int insertUser(User user);
	
	// 내 정보 조회
	// SELECT * FROM USER_ID = #{userId}
	User findById(@Param("userId") Integer userId);
	
    // 비밀번호 재설정 시, 로그인 아이디(loginId)로 DB의 PASSWORD를 업데이트
    void updatePassword(@Param("loginId") String loginId,
                        @Param("newPassword") String newPassword);
	

}
