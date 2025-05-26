package com.hobby.challenge.fobackend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hobby.challenge.fobackend.dto.CreateParticipationDTO;
import com.hobby.challenge.fobackend.dto.ParticipationResponseDTO;
import com.hobby.challenge.fobackend.service.ParticipationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ParticipationController {
	private final ParticipationService participationService;
	
    // 참여 요청
	@PostMapping("/challenges/{challengeId}/participations")
	public ResponseEntity<CreateParticipationDTO> requestJoin(
	    @PathVariable("challengeId") Integer challengeId,
	    @AuthenticationPrincipal(expression="userId") Integer userId
	) {
	    CreateParticipationDTO result = participationService.requestJoin(userId, challengeId);
	    return ResponseEntity.status(HttpStatus.CREATED).body(result);
	}

    // 내 참여 내역
    @GetMapping("/users/{userId}/participations")
    public ResponseEntity<List<ParticipationResponseDTO>> getMyParticipations(@PathVariable("userId") Integer userId) {
        return ResponseEntity.ok(participationService.getMyParticipations(userId));
    }

    // 챌린지별 요청 목록 (OWNER)
    @GetMapping("/challenges/{challengeId}/participations")
    public ResponseEntity<List<ParticipationResponseDTO>> getRequests(@PathVariable("challengeId") Integer challengeId) {
        return ResponseEntity.ok(participationService.getRequests(challengeId));
    }

    // 상태 변경
    @PatchMapping("/participations/{participationId}/status")
    public ResponseEntity<ParticipationResponseDTO> changeStatus(
            @PathVariable("participationId") Integer participationId,
            @RequestParam("status") String status) {
    	ParticipationResponseDTO updated = participationService.changeStatus(participationId, status);
    	return ResponseEntity.ok(updated);
    }


}
