package com.hobby.challenge.fobackend.mapper;


import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.hobby.challenge.fobackend.dto.UserRankingDTO;

@Mapper
public interface RankingMapper {
    // 사용자별 인증 수 랭킹 조회
    List<UserRankingDTO> findUsersByCertificationCount(@Param("limit") int limit);
}
