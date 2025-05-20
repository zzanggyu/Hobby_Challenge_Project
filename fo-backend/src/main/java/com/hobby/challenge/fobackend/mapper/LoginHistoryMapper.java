package com.hobby.challenge.fobackend.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

import com.hobby.challenge.fobackend.entity.LoginHistory;

@Mapper
public interface LoginHistoryMapper {
	// 로그인 이력 생성 ( 로그인 성공 시)
	@Insert("INSERT INTO LOGIN_HISTORY ("
			+ "USER_ID,"
			+ "LOGIN_DATE)"
			+ "VALUES (#{userId}, #{loginDate})")
	void insertLoginHistory(LoginHistory loginHistory);
	
}
