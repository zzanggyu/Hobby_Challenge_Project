package com.hobby.challenge.fobackend.service;

import java.util.List;

import com.hobby.challenge.fobackend.dto.CreateParticipationDTO;
import com.hobby.challenge.fobackend.dto.ParticipationResponseDTO;

public interface ParticipationService {
	// 챌린지 참여 요청
	CreateParticipationDTO  requestJoin(Integer userId, Integer challengeId);
    
    // 챌리지의 모든 요청 조회( OWNER 챌린지 생성자 용)
    List<ParticipationResponseDTO> getRequests(Integer challengeId);
    
    // 내 참여 내역 조회
    List<ParticipationResponseDTO> getMyParticipations(Integer userId);
    
    // 참여 상태 업데이트
    ParticipationResponseDTO changeStatus(Integer participationId, String status);
    
    // 참여 요청 취소 및 챌린지 탈퇴
    void cancelParticipation(Integer userId, Integer participationId);

}
