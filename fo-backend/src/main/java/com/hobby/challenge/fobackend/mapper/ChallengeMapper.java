package com.hobby.challenge.fobackend.mapper;

import java.time.LocalDate;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.hobby.challenge.fobackend.dto.ChallengeDetailDTO;
import com.hobby.challenge.fobackend.entity.Challenge;

@Mapper
public interface ChallengeMapper {
	
	// 챌린지 생성
	void insertChallenge(Challenge challenge);
	
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
	 
	// 단건 조회 참여한 챌린지 상세
	Challenge selectById(
		    @Param("challengeId") Integer challengeId,
		    @Param("userId")      Integer userId
		   
		);
	
	// 전체 챌린지 조회
//	List<Challenge> findAll();
	

	 
    // 상세 조회용
    ChallengeDetailDTO selectDetailById(
    	      @Param("challengeId") Integer challengeId,
    	      @Param("userId")      Integer userId
    	    );
    
    // 챌린지 수정 챌린지 생성자용
    int updateChallenge(Challenge challenge);
	
    // 챌린지 삭제 챌린지 생성자용
    int softDeleteChallenge(@Param("challengeId") Integer challengeId);
}
