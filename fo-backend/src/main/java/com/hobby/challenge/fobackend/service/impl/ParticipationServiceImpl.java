package com.hobby.challenge.fobackend.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.hobby.challenge.fobackend.dto.ParticipationResponseDTO;
import com.hobby.challenge.fobackend.mapper.ParticipationMapper;
import com.hobby.challenge.fobackend.service.ParticipationService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ParticipationServiceImpl implements ParticipationService {
	private final ParticipationMapper participationMapper;

	// 챌린지 참여 요청
	@Override
	public ParticipationResponseDTO requestJoin(Integer userId, Integer challengeId) {
        // 1. DTO 생성 및 insert
        ParticipationResponseDTO dto = ParticipationResponseDTO.builder()
            .userId(userId)
            .challengeId(challengeId)
            .build();
        participationMapper.insertParticipation(dto);
        // useGeneratedKeys 설정으로 dto.participationId에 자동 세팅
        return dto;
    }
	

	// 챌리지의 모든 요청 조회( OWNER 챌린지 생성자 용)
	@Override
	public List<ParticipationResponseDTO> getRequests(Integer challengeId) {
		return participationMapper.findRequestsByChallenge(challengeId);
	}

	// 내 참여 내역 조회
	@Override
	public List<ParticipationResponseDTO> getMyParticipations(Integer userId) {
		return participationMapper.findByUser(userId);
	}

	// 참여 상태 업데이트
	@Override
	public ParticipationResponseDTO changeStatus(Integer participationId, String status) {
        participationMapper.updateStatus(participationId, status);
        return participationMapper.selectById(participationId);
	}

	// 승인된 참여자 조회
	@Override
	public List<ParticipationResponseDTO> getApprovedParticipants(Integer challengeId) {
		return participationMapper.findApprovedByChallenge(challengeId);
	}

}
