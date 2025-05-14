package com.hobby.challenge.fobackend.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.hobby.challenge.fobackend.entity.User;

@Mapper
public interface UserMapper {
	// 회원 조회 
	// SELECT * FROM USER_ID = #{userId}
	User findById(@Param("userId") Integer userId);
	
	// 로그인 아이디로 조회 
	// SELECT * FROM USER WHERE LOGIN_ID = #{loginId}
	User findByLoginId(@Param("loginId") String loginId);
	
	// 회원 등록
	// INSERT INTO USER (LOGIN_ID, USERNAME, PASSWORD, EMAIL, NICKNAME, ROLE)
	// VALUES (#{loginId}, #{username}, #{password}, #{email}, #{nickname}, #{role})
	int insertUser(User user);
}
