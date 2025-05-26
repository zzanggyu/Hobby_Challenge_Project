package com.hobby.challenge.fobackend.service.impl;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.hobby.challenge.fobackend.dto.CreateParticipationDTO;
import com.hobby.challenge.fobackend.dto.ParticipationResponseDTO;
import com.hobby.challenge.fobackend.entity.Challenge;
import com.hobby.challenge.fobackend.mapper.ChallengeMapper;
import com.hobby.challenge.fobackend.mapper.ParticipationMapper;
import com.hobby.challenge.fobackend.service.ParticipationService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ParticipationServiceImpl implements ParticipationService {
	private final ParticipationMapper participationMapper;
	private final ChallengeMapper challengeMapper;

	// 챌린지 참여 요청
	@Override
	public CreateParticipationDTO requestJoin(Integer userId, Integer challengeId) {
		Challenge c = challengeMapper.selectById(challengeId, null);
	    // 이미 요청/승인된 내역이 있는지 확인
	    if (participationMapper.selectByUserAndChallenge(userId, challengeId) != null) {
	        throw new IllegalStateException("이미 이 챌린지에 참여하셨습니다.");
	    }
	    // 진행 중인 챌린지인 경우에만 참여가능 종료됐거나 삭제됐거나 확인
	    if (c.getEndDate().isBefore(LocalDate.now()) || Boolean.TRUE.equals(c.getIsDeleted())) {
	        throw new IllegalStateException("종료되었거나 삭제된 챌린지에는 참여할 수 없습니다.");
	    }
	    
        // DTO 생성 및 insert
        CreateParticipationDTO dto = CreateParticipationDTO.builder()
            .userId(userId)
            .challengeId(challengeId)
            .status("REQUESTED")
            .role("MEMBER")
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



}
