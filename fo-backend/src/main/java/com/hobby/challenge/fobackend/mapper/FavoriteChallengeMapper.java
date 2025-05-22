package com.hobby.challenge.fobackend.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.hobby.challenge.fobackend.dto.FavoriteChallengeDTO;
import com.hobby.challenge.fobackend.entity.FavoriteChallenge;

@Mapper
public interface FavoriteChallengeMapper {
	// 관심 챌린지 목록 조회
    List<FavoriteChallengeDTO> findAllFavorites(@Param("userId") int userId);
    
    // 관심 챌린지에 이미 저장했는지 확인하기 위해 카운트 1이면 있고 0이면 없음
    int countFavoriteChallenge(@Param("userId") int userId,
            @Param("challengeId") int challengeId);
    
    // 관심 챌린지 추가
    void insertFavoriteChallenge(FavoriteChallenge favorite);
    
    // 관심 챌린지 삭제
    void deleteFavoriteChallenge(FavoriteChallenge favorite);
}
