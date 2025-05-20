package com.hobby.challenge.fobackend.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hobby.challenge.fobackend.dto.CreateChallengeRequestDTO;
import com.hobby.challenge.fobackend.entity.Challenge;
import com.hobby.challenge.fobackend.mapper.ChallengeMapper;
import com.hobby.challenge.fobackend.service.ChallengeService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChallengeServiceImpl implements ChallengeService{
	private final ChallengeMapper challengeMapper;
	

    @Override
    @Transactional // 중간에 예외가 나면 롤백시킴
    public Challenge createChallenge(CreateChallengeRequestDTO dto, Integer creatorId) {
        // DTO → Entity 변환
    	Challenge c = Challenge.builder()
    		    .title(dto.getTitle())
    		    .description(dto.getDescription())
    		    .startDate(dto.getStartDate())
    		    .endDate(dto.getEndDate())
    		    .categoryId(dto.getCategoryId())
    		    .createdBy(creatorId)
    		    .build();

        // DB 삽입
        challengeMapper.insertChallenge(c);

        // 생성된 challengeId를 포함한 객체 반환
        return c;
    }
	
}
