package com.hobby.challenge.fobackend.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.hobby.challenge.fobackend.dto.ParticipationResponseDTO;

@Mapper
public interface ParticipationMapper {
    // 참여 요청
	void insertParticipation(ParticipationResponseDTO dto);

    // 특정 챌린지의 모든 요청 조회 (OWNER 챌린지 생성자용)
    List<ParticipationResponseDTO> findRequestsByChallenge(@Param("challengeId") Integer challengeId);

    // 특정 사용자의 참여 내역 조회
    List<ParticipationResponseDTO> findByUser(@Param("userId") Integer userId);

    // 상태 업데이트 (승인/거절)
    void updateStatus(@Param("participationId") Integer participationId,
                      @Param("status") String status);

    // 승인된 참여자 조회
    List<ParticipationResponseDTO> findApprovedByChallenge(@Param("challengeId") Integer challengeId);
    
    // 참여 테이블 조회
    ParticipationResponseDTO selectById(@Param("participationId") Integer participationId);
}
