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
	
	// 관심 챌린지 목록 가져오기
    @Override
    public List<FavoriteChallengeDTO> getMyFavorites(int userId) {
        return favMapper.findAllFavorites(userId);
    }	

    // 관심 챌린지 토글 추가/삭제
    @Override
    @Transactional
    public void toggleFavorite(int userId, int challengeId) {
        boolean exists = favMapper.countFavoriteChallenge(userId, challengeId) > 0;
        if (exists) {
            // 이미 추가 되어 있으면 제거
            favMapper.deleteFavoriteChallenge(new FavoriteChallenge(userId, challengeId, null));
        } else {
            // 아직 없으면 추가
            favMapper.insertFavoriteChallenge(new FavoriteChallenge(userId, challengeId, null));
        }
    }
    
    // 관심 챌린지 + 참여요청 중인 챌린지를 함께 조회
    @Override
    public List<FavoriteChallengeDTO> getFavoritesAndRequestedChallenges(int userId) {
        // 1) 기존 관심 챌린지 목록
        List<FavoriteChallengeDTO> favorites = favMapper.findAllFavorites(userId);
        
        // 2) 참여요청 중인 챌린지 목록 가져오기
        List<ParticipationResponseDTO> requestedParticipations = 
            participationMapper.findByUserAndStatus(userId, "REQUESTED");
        
        // 3) 참여요청 중인 챌린지를 FavoriteChallengeDTO 형태로 변환
        List<Integer> favoriteIds = favorites.stream()
            .map(fav -> fav.getChallengeId())
            .collect(Collectors.toList());
        
        for (ParticipationResponseDTO participation : requestedParticipations) {
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
                        .build();
                    
                    // FavoriteChallengeDTO 생성 (참여요청 중임을 표시)
                    FavoriteChallengeDTO requestedFav = FavoriteChallengeDTO.builder()
                        .userId(userId)
                        .challengeId(challengeId)
                        .createdDate(participation.getRequestDate()) // 요청 날짜 사용
                        .challenge(challengeDto)
                        .build();
                    
                    favorites.add(requestedFav);
                }
            }
        }
        
        // 4) 최신 순으로 정렬 (관심 등록일/참여요청일 기준)
        return favorites.stream()
            .sorted((a, b) -> b.getCreatedDate().compareTo(a.getCreatedDate()))
            .collect(Collectors.toList());
    }

}
