package com.hobby.challenge.fobackend.service;

import java.util.List;

import com.hobby.challenge.fobackend.dto.UserRankingDTO;

public interface RankingService {
    // 전체 인증 수 기준 챌린지 랭킹 조회
    // 사용자별 인증 수 랭킹 조회
    List<UserRankingDTO> getUserRankingsByCertificationCount(int limit);
}