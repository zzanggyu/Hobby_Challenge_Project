package com.hobby.challenge.fobackend.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hobby.challenge.fobackend.dto.ChallengeResponseDTO;
import com.hobby.challenge.fobackend.dto.FavoriteChallengeDTO;
import com.hobby.challenge.fobackend.dto.ParticipationResponseDTO;
import com.hobby.challenge.fobackend.entity.Challenge;
import com.hobby.challenge.fobackend.entity.FavoriteChallenge;
import com.hobby.challenge.fobackend.exception.CustomException;
import com.hobby.challenge.fobackend.exception.ErrorCode;
import com.hobby.challenge.fobackend.mapper.ChallengeMapper;
import com.hobby.challenge.fobackend.mapper.FavoriteChallengeMapper;
import com.hobby.challenge.fobackend.mapper.ParticipationMapper;
import com.hobby.challenge.fobackend.service.FavoriteChallengeService;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class FavoriteChallengeServiceImpl implements FavoriteChallengeService{
	
	private final FavoriteChallengeMapper favMapper;
	private final ParticipationMapper participationMapper;
	private final ChallengeMapper challengeMapper;
    //  관심 챌린지 개수 제한 상수
    private static final int MAX_FAVORITE_COUNT = 10;
	
	// 관심 챌린지 목록 가져오기
    @Override
    public List<FavoriteChallengeDTO> getMyFavorites(int userId) {
        return favMapper.findAllFavorites(userId);
    }	

    
    // 관심 챌린지 + 참여중인 챌린지 + 참여 요청중인 챌린지를 함께 조회
    @Override
    public List<FavoriteChallengeDTO> getFavoritesAndRequestedChallenges(int userId) {
        //  기존 관심 챌린지 목록
        List<FavoriteChallengeDTO> favorites = favMapper.findAllFavorites(userId);
        
        //  참여요청 중인 챌린지 목록 가져오기
        List<ParticipationResponseDTO> requestedParticipations = 
            participationMapper.findByUserAndStatus(userId, "REQUESTED");
            
        //  참여중인 챌린지 목록도 가져오기
        List<ParticipationResponseDTO> approvedParticipations = 
            participationMapper.findByUserAndStatus(userId, "APPROVED");
        
        //  이미 관심 챌린지에 있는 ID 목록 수집
        List<Integer> favoriteIds = favorites.stream()
            .map(fav -> fav.getChallengeId())
            .collect(Collectors.toList());
        
        // 요청중인 챌린지를 관심목록에 추가
        addParticipationsToFavorites(favorites, requestedParticipations, favoriteIds, userId, false);
        
        // 참여중인 챌린지를 관심목록에 추가
        addParticipationsToFavorites(favorites, approvedParticipations, favoriteIds, userId, true);
        
        //  최신 순으로 정렬 (관심 등록일/참여요청일 기준)
        return favorites.stream()
            .sorted((a, b) -> b.getCreatedDate().compareTo(a.getCreatedDate()))
            .collect(Collectors.toList());
    }
    
    // 참여 목록을 관심 목록에 추가하는 헬퍼 메서드
    private void addParticipationsToFavorites(
            List<FavoriteChallengeDTO> favorites,
            List<ParticipationResponseDTO> participations,
            List<Integer> favoriteIds,
            int userId,
            boolean isApproved) {
        
        for (ParticipationResponseDTO participation : participations) {
            Integer challengeId = participation.getChallengeId();
            
            // 이미 관심 챌린지에 있으면 스킵 (중복 방지)
            if (!favoriteIds.contains(challengeId)) {
                // 챌린지 상세 정보 가져오기
                Challenge challenge = challengeMapper.selectById(challengeId, userId);
                if (challenge != null && !Boolean.TRUE.equals(challenge.getIsDeleted())) {
                    
                    // ChallengeResponseDTO 생성
                    ChallengeResponseDTO challengeDto = ChallengeResponseDTO.builder()
                        .challengeId(challenge.getChallengeId())
                        .title(challenge.getTitle())
                        .description(challenge.getDescription())
                        .categoryId(challenge.getCategoryId())
                        .startDate(challenge.getStartDate())
                        .endDate(challenge.getEndDate())
                        .createdDate(challenge.getCreatedDate())
                        .creatorNickname(challenge.getCreator().getNickname())
                        .isFavorite(challenge.getIsFavorite())
                        .favoriteCount(challenge.getFavoriteCount()) 
                        .participantCount(challenge.getParticipantCount())
                        .build();
                    
                    // FavoriteChallengeDTO 생성
                    FavoriteChallengeDTO participationFav = FavoriteChallengeDTO.builder()
                        .userId(userId)
                        .challengeId(challengeId)
                        .createdDate(participation.getRequestDate()) // 요청/참여 날짜 사용
                        .challenge(challengeDto)
                        .build();
                    
                    // 참여 상태 표시를 위한 플래그 추가
                    participationFav.setParticipating(isApproved); // 참여중 여부
                    participationFav.setRequesting(!isApproved);   // 요청중 여부
                    
                    favorites.add(participationFav);
                    favoriteIds.add(challengeId); // 중복 방지를 위해 추가
                }
            }
        }
    }
    
    // 관심 챌린지 토글 - 10개 제한 추가
    @Override
    @Transactional
    public void toggleFavorite(int userId, int challengeId) {
        boolean exists = favMapper.countFavoriteChallenge(userId, challengeId) > 0;
        
        if (exists) {
            // 이미 추가되어 있으면 제거
            favMapper.deleteFavoriteChallenge(new FavoriteChallenge(userId, challengeId, null));
        } else {
            // ✅ 관심 챌린지 개수 확인 (10개 제한)
            int currentCount = favMapper.findAllFavorites(userId).size();
            if (currentCount >= MAX_FAVORITE_COUNT) {
                throw new CustomException(
                    ErrorCode.FAVORITE_LIMIT_EXCEEDED, 
                    "관심 챌린지는 최대 " + MAX_FAVORITE_COUNT + "개까지만 등록할 수 있습니다."
                );
            }
            
            // 아직 없으면 추가
            favMapper.insertFavoriteChallenge(new FavoriteChallenge(userId, challengeId, null));
        }
    }

}
