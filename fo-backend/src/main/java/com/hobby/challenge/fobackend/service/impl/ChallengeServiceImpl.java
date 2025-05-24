package com.hobby.challenge.fobackend.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hobby.challenge.fobackend.dto.ChallengeResponseDTO;
import com.hobby.challenge.fobackend.dto.CreateChallengeRequestDTO;
import com.hobby.challenge.fobackend.dto.PageResponseDTO;
import com.hobby.challenge.fobackend.entity.Challenge;
import com.hobby.challenge.fobackend.mapper.ChallengeMapper;
import com.hobby.challenge.fobackend.service.ChallengeService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChallengeServiceImpl implements ChallengeService{
	private final ChallengeMapper challengeMapper;
	
	// 페이징 전체 챌린지 조회
//	@Override
//	public List<ChallengeResponseDTO> getAllChallenges(Integer userId, int size, int offset) {
//	  return challengeMapper.findAllWithPaging(userId, size, offset).stream()
//	    .map(c -> ChallengeResponseDTO.builder()
//	        .challengeId(c.getChallengeId())
//	        .title(c.getTitle())
//	        .description(c.getDescription())
//	        .categoryId(c.getCategoryId()) 
//	        .creatorNickname(c.getCreator().getNickname())
//	        .startDate(c.getStartDate())
//	        .endDate(c.getEndDate())
//	        .createdDate(c.getCreatedDate())
//	        .isFavorite(c.getIsFavorite()) 
//	        .build())
//	    .toList();
//	}
	
	// 전체 챌린지 수 가져오기
	@Override
	public int countAllChallenges(String search, Integer categoryId) {
	    return challengeMapper.countAll(search, categoryId);
	}
	
	
	

	@Override
	public PageResponseDTO<ChallengeResponseDTO> getPageChallenges(
	        Integer userId,
	        String search,
	        Integer categoryId,
	        int size,
	        int offset) {

	    // 페이징 + 필터된 리스트 조회 후 DTO 변환
	    List<ChallengeResponseDTO> items = challengeMapper
	        .findAllWithPaging(userId, search, categoryId, size, offset)
	        .stream()
	        .map(c -> ChallengeResponseDTO.builder()
	            .challengeId(c.getChallengeId())
	            .title(c.getTitle())
	            .description(c.getDescription())
	            .categoryId(c.getCategoryId())
	            .creatorNickname(c.getCreator().getNickname())
	            .startDate(c.getStartDate())
	            .endDate(c.getEndDate())
	            .createdDate(c.getCreatedDate())
	            .isFavorite(c.getIsFavorite())
	            .build()
	        )
	        .toList();

	    // 검색/카테고리 조건을 반영한 총 개수 조회
	    int totalCount = challengeMapper.countAll(search, categoryId);

	    // 페이징 응답 DTO 반환
	    return new PageResponseDTO<>(totalCount, items);
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
