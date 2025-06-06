package com.hobby.challenge.fobackend.service;

import java.util.List;

import com.hobby.challenge.fobackend.dto.FavoriteChallengeDTO;

public interface FavoriteChallengeService {
	// 관심 챌린지 목록 조회
    List<FavoriteChallengeDTO> getMyFavorites(int userId);
    
    // 관심 챌린지 + 참여요청 중인 챌린지를 함께 조회
    List<FavoriteChallengeDTO> getFavoritesAndRequestedChallenges(int userId);
    
    // 관심 챌린지 토글 추가 삭제
    void toggleFavorite(int userId, int challengeId);

}
