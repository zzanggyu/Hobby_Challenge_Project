package com.hobby.challenge.fobackend.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.hobby.challenge.fobackend.dto.CreateParticipationDTO;
import com.hobby.challenge.fobackend.dto.ParticipantDTO;
import com.hobby.challenge.fobackend.dto.ParticipationResponseDTO;

@Mapper
public interface ParticipationMapper {
    // 참여 요청
	void insertParticipation(CreateParticipationDTO dto);

    // 특정 챌린지의 모든 요청 조회 (OWNER 챌린지 생성자용)
    List<ParticipationResponseDTO> findRequestsByChallenge(@Param("challengeId") Integer challengeId);

    // 특정 사용자의 참여 내역 조회
    List<ParticipationResponseDTO> findByUser(@Param("userId") Integer userId);
    
    // 특정 사용자의 특정 상태 참여 내역 조회
    List<ParticipationResponseDTO> findByUserAndStatus(
        @Param("userId") Integer userId, 
        @Param("status") String status
    );

    // 상태 업데이트 (승인/거절)
    void updateStatus(@Param("participationId") Integer participationId,
                      @Param("status") String status);

    // 참여 테이블 조회
    ParticipationResponseDTO selectById(@Param("participationId") Integer participationId);
    
    // 가입 승인된 참여자 목록 (챌린지 상세용)
    List<ParticipantDTO> findApprovedByChallenge(
            @Param("challengeId") Integer challengeId
        );
    
    // 승인된 참여 조회 
    ParticipationResponseDTO selectByUserAndChallenge(
    	    @Param("userId") Integer userId,
    	    @Param("challengeId") Integer challengeId
    	);
    
    // 이미 요청 중인 또는 승인된 내역 조회 (중복 요청 방지용) 같은 챌린지에 대한
    ParticipationResponseDTO selectByUserAndChallengeAnyStatus(
        @Param("userId") Integer userId,
        @Param("challengeId") Integer challengeId
    );
    
    // REQUESTED 또는 APPROVED 상태인 참여 건수 모든 챌린지에 대해 하나만 요청하고 참여 가능
    int countActiveParticipations(@Param("userId") Integer userId);
    
    // 특정 유저 참여 삭제 (요청 취소 / 탈퇴용)
    int deleteByIdAndUser(
        @Param("participationId") Integer participationId,
        @Param("userId") Integer userId
    );
}
