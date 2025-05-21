package com.hobby.challenge.fobackend.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hobby.challenge.fobackend.dto.ChallengeResponseDTO;
import com.hobby.challenge.fobackend.dto.CreateChallengeRequestDTO;
import com.hobby.challenge.fobackend.entity.Challenge;
import com.hobby.challenge.fobackend.mapper.ChallengeMapper;
import com.hobby.challenge.fobackend.service.ChallengeService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChallengeServiceImpl implements ChallengeService{
	private final ChallengeMapper challengeMapper;
	
	// 전체 챌린지 조회
	@Override
	public List<ChallengeResponseDTO> getAllChallenges() {
	  return challengeMapper.findAll().stream()
	    .map(c -> ChallengeResponseDTO.builder()
	        .challengeId(c.getChallengeId())
	        .title(c.getTitle())
	        .description(c.getDescription())
	        .categoryId(c.getCategoryId()) 
	        .creatorNickname(c.getCreator().getNickname())
	        .startDate(c.getStartDate())
	        .endDate(c.getEndDate())
	        .createdDate(c.getCreatedDate())
	        .build())
	    .toList();
	}
	
	// 챌린지 생성 
    @Override
    @Transactional // 중간에 예외가 나면 롤백시킴
    public ChallengeResponseDTO createChallenge(CreateChallengeRequestDTO dto, Integer userId) {
        // DTO → Entity 변환
    	Challenge c = Challenge.builder()
    		    .title(dto.getTitle())
    		    .description(dto.getDescription())
    		    .startDate(dto.getStartDate())
    		    .endDate(dto.getEndDate())
    		    .categoryId(dto.getCategoryId())
    		    .createdBy(userId)
    		    .build();

        // DB 삽입
        challengeMapper.insertChallenge(c);

        // Entity → ResponseDTO 변환
        return ChallengeResponseDTO.builder()
            .challengeId(c.getChallengeId())
            .title(c.getTitle())
            .description(c.getDescription())
            .startDate(c.getStartDate())
            .endDate(c.getEndDate())
            .categoryId(c.getCategoryId())
            .createdDate(c.getCreatedDate())
            .build();
    }
    
    
	
}
