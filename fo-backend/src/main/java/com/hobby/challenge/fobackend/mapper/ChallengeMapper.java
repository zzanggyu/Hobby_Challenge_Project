package com.hobby.challenge.fobackend.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.hobby.challenge.fobackend.entity.Challenge;

@Mapper
public interface ChallengeMapper {
	
	// 전체 챌린지 조회
	List<Challenge> findAll();
	
	// 챌린지 생성
	void insertChallenge(Challenge challenge);
	

}
