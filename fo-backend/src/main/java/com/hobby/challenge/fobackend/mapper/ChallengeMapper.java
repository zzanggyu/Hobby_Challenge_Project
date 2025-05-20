package com.hobby.challenge.fobackend.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.hobby.challenge.fobackend.entity.Challenge;

@Mapper
public interface ChallengeMapper {
	
	// 챌린지 생성
	void insertChallenge(Challenge challenge);
}
