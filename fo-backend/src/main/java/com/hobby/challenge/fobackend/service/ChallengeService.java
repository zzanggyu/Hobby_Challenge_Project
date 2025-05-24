package com.hobby.challenge.fobackend.service;

import java.util.List;

import com.hobby.challenge.fobackend.dto.ChallengeResponseDTO;
import com.hobby.challenge.fobackend.dto.CreateChallengeRequestDTO;
import com.hobby.challenge.fobackend.dto.PageResponseDTO;

public interface ChallengeService {
	
	// 30개씩 페이징한 챌린지 목록 조회
//	List<ChallengeResponseDTO> getAllChallenges(Integer userId, int size, int offset);
	
	// 전체 챌린지 목록 조회  
//	List<ChallengeResponseDTO> getAllChallenges();
	
	// 전체 챌린지 수 
	int countAllChallenges(String search, Integer categoryId);
	
	
	// 제목 검색 + 카테고리 필터 적용 첼린지 조회
	PageResponseDTO<ChallengeResponseDTO> getPageChallenges(			
				Integer userId,
				String search,
			    Integer categoryId, 
			    int size, 
			    int offset
		    );
	
	// 챌린지 생성
	ChallengeResponseDTO createChallenge(CreateChallengeRequestDTO dto, Integer userId);
	

}
