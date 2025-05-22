package com.hobby.challenge.fobackend.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hobby.challenge.fobackend.dto.FavoriteChallengeDTO;
import com.hobby.challenge.fobackend.entity.FavoriteChallenge;
import com.hobby.challenge.fobackend.mapper.FavoriteChallengeMapper;
import com.hobby.challenge.fobackend.service.FavoriteChallengeService;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class FavoriteChallengeServiceImpl implements FavoriteChallengeService{
	
	private final FavoriteChallengeMapper favMapper;
	
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

}
