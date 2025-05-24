package com.hobby.challenge.fobackend.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.hobby.challenge.fobackend.entity.Challenge;

@Mapper
public interface ChallengeMapper {
	
	// 30개씩 페이징 챌린지 목록 조회
	List<Challenge> findAllWithPaging(
			// 페이징 + 검색 + 카테고리
		    @Param("userId") Integer userId,
		    @Param("search") String search,
		    @Param("categoryId") Integer categoryId,
		    @Param("size") int size,
		    @Param("offset") int offset
		);
	
	// // 전체 개수(검색+카테고리 반영)
	int countAll(    
//			@Param("userId") Integer userId,
		    @Param("search") String search,
		    @Param("categoryId") Integer categoryId
		);
	
	// 전체 챌린지 조회
//	List<Challenge> findAll();
	
	// 챌린지 생성
	void insertChallenge(Challenge challenge);
	

}
