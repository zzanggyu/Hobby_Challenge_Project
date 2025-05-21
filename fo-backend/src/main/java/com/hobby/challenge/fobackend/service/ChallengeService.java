package com.hobby.challenge.fobackend.service;

import java.util.List;

import com.hobby.challenge.fobackend.dto.ChallengeResponseDTO;
import com.hobby.challenge.fobackend.dto.CreateChallengeRequestDTO;

public interface ChallengeService {
	
	// 전체 챌린지 목록 조회 
	List<ChallengeResponseDTO> getAllChallenges();
	
	// 챌린지 생성
	ChallengeResponseDTO createChallenge(CreateChallengeRequestDTO dto, Integer userId);
	

}
