package com.hobby.challenge.fobackend.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hobby.challenge.fobackend.dto.ChallengeDetailDTO;
import com.hobby.challenge.fobackend.dto.ChallengeResponseDTO;
import com.hobby.challenge.fobackend.dto.CreateChallengeRequestDTO;
import com.hobby.challenge.fobackend.dto.CreateParticipationDTO;
import com.hobby.challenge.fobackend.dto.PageResponseDTO;
import com.hobby.challenge.fobackend.dto.ParticipantDTO;
import com.hobby.challenge.fobackend.dto.UpdateChallengeRequestDTO;
import com.hobby.challenge.fobackend.entity.Challenge;
import com.hobby.challenge.fobackend.exception.CustomException;
import com.hobby.challenge.fobackend.exception.ErrorCode;
import com.hobby.challenge.fobackend.mapper.ChallengeMapper;
import com.hobby.challenge.fobackend.mapper.ParticipationMapper;
import com.hobby.challenge.fobackend.service.ChallengeService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChallengeServiceImpl implements ChallengeService{
	private final ChallengeMapper challengeMapper;
	private final ParticipationMapper participationMapper;
	
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

        // DB 삽입 챌린지 저장
        challengeMapper.insertChallenge(c);
        Integer newChallengeId = c.getChallengeId();
        
        
        // OWNER 권한으로 참여 테이블에 추가
        CreateParticipationDTO ownerDto = CreateParticipationDTO.builder()
                 .userId(userId)
                 .challengeId(newChallengeId)
                 .status("APPROVED")
                 .role("OWNER")
                 .build();
        participationMapper.insertParticipation(ownerDto);
        
        // Entity -> ChallengeResponseDTO 변환
        return ChallengeResponseDTO.builder()
            .challengeId(c.getChallengeId())
            .title(c.getTitle())
            .description(c.getDescription())
            .startDate(c.getStartDate())
            .endDate(c.getEndDate())
            .categoryId(c.getCategoryId())
            .creatorNickname(c.getCreatedBy().toString())
            .createdDate(c.getCreatedDate())
            .build();
    }
    
	// 전체 챌린지 수 가져오기
	@Override
	public int countAllChallenges(String search, Integer categoryId) {
	    return challengeMapper.countAll(search, categoryId);
	}
	
	// 페이징 된 챌린지 가져오기
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
	
	// 단건 조회 참여한 챌린지 상세
    @Override
    public ChallengeResponseDTO findById(Integer challengeId) {
        // 매퍼에서 resultMap="ChallengeWithCreator" 로 creator.nickname 까지 채워온 후
        Challenge c = challengeMapper.selectById(challengeId ,null);

        // DTO 에 정의된 필드만 사용해서 빌더를 채워줍니다
        return ChallengeResponseDTO.builder()
            .challengeId(c.getChallengeId())
            .title(c.getTitle())
            .description(c.getDescription())
            .categoryId(c.getCategoryId())
            .startDate(c.getStartDate())
            .endDate(c.getEndDate())
            .createdDate(c.getCreatedDate())
//            .isFavorite(c.getIsFavorite()) 참여한 챌린지 조회이기 때문에 관심여부 필요없음
            .creatorNickname(c.getCreator().getNickname())
            .createdBy(c.getCreatedBy())
            .build();
    }
	

    
    // 챌린지 상세 조회
    @Override
    public ChallengeDetailDTO getChallengeDetail(Integer challengeId, Integer userId) {
        return challengeMapper.selectDetailById(challengeId, userId);
    }
    
    // 참여 승인된 참여자 목록 조회
    @Override
    public List<ParticipantDTO> getApprovedParticipants(Integer challengeId) {
        return participationMapper.findApprovedByChallenge(challengeId);
    }
    
    // 챌린지 수정 챌린지 생성자(owner)용
    @Override
    @Transactional
    public ChallengeResponseDTO updateChallenge(Integer challengeId,
                                                UpdateChallengeRequestDTO dto,
                                                Integer userId) {

        Challenge c = challengeMapper.selectById(challengeId, null);

        // 권한 체크
        if (!c.getCreatedBy().equals(userId)) {
            throw new CustomException(ErrorCode.CHALLENGE_UPDATE_FORBIDDEN, "챌린지 생성자만 수정 가능합니다");
        }

        // 원래 정보에 엔티티 값 덮어쓰기
        c.setTitle(dto.getTitle());
        c.setDescription(dto.getDescription());
        c.setStartDate(dto.getStartDate());
        c.setEndDate(dto.getEndDate());
        c.setCategoryId(dto.getCategoryId());

        // UPDATE
        challengeMapper.updateChallenge(c);

        // 응답 DTO 변환
        return ChallengeResponseDTO.builder()
                .challengeId(c.getChallengeId())
                .title(c.getTitle())
                .description(c.getDescription())
                .categoryId(c.getCategoryId())
                .startDate(c.getStartDate())
                .endDate(c.getEndDate())
                .createdDate(c.getCreatedDate())
                .createdBy(c.getCreatedBy())
                .creatorNickname(c.getCreatedBy().toString()) 
                .build();
    }

    // 챌린지 삭제 챌린지 생성자(owner)용 
    @Override
    @Transactional
    public void deleteChallenge(Integer challengeId, Integer userId) {
        // 챌린지 조회
        Challenge c = challengeMapper.selectById(challengeId, userId);
        int rows = challengeMapper.softDeleteChallenge(challengeId);
        
        if (c == null || Boolean.TRUE.equals(c.getIsDeleted())) {
            throw new CustomException(ErrorCode.CHALLENGE_NOT_FOUND);
        }

        // 권한 확인
        if (!c.getCreatedBy().equals(userId)) {
            throw new CustomException(ErrorCode.CHALLENGE_DELETE_FORBIDDEN,
                                      "챌린지 생성자만 삭제할 수 있습니다.");
        }
        
        // 삭제처리 소프트 딜리트
        if (rows == 0) {
            throw new CustomException(ErrorCode.CHALLENGE_NOT_FOUND,
                                      "이미 삭제되었거나 존재하지 않는 챌린지입니다.");
        }
    }
}
