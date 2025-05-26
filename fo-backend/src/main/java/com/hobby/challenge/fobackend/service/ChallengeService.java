package com.hobby.challenge.fobackend.service;

import java.util.List;

import com.hobby.challenge.fobackend.dto.ChallengeDetailDTO;
import com.hobby.challenge.fobackend.dto.ChallengeResponseDTO;
import com.hobby.challenge.fobackend.dto.CreateChallengeRequestDTO;
import com.hobby.challenge.fobackend.dto.PageResponseDTO;
import com.hobby.challenge.fobackend.dto.ParticipantDTO;
import com.hobby.challenge.fobackend.dto.UpdateChallengeRequestDTO;

public interface ChallengeService {
	
	// 30개씩 페이징한 챌린지 목록 조회
//	List<ChallengeResponseDTO> getAllChallenges(Integer userId, int size, int offset);
	
	// 전체 챌린지 목록 조회  
//	List<ChallengeResponseDTO> getAllChallenges();
	
	// 챌린지 생성
	ChallengeResponseDTO createChallenge(CreateChallengeRequestDTO dto, Integer userId);
	
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
	
	// 참여한 챌린지 상세 조회
	ChallengeResponseDTO findById(Integer challengeId);
	
	// 상세 조회용
	ChallengeDetailDTO getChallengeDetail(Integer challengeId, Integer userId);
	
	// 챌린지 참여 승인된 참여자 목록 조회
	List<ParticipantDTO> getApprovedParticipants(Integer challengeId);
	
	// 챌린지 수정 챌린지 생성자(owner)용 
	ChallengeResponseDTO updateChallenge(Integer challengeId,
            UpdateChallengeRequestDTO dto,
            Integer userId);
	
	// 챌린지 삭제 챌린지 생성자(owner)용 
	void deleteChallenge(Integer challengeId, Integer userId);
}
