package com.hobby.challenge.fobackend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hobby.challenge.fobackend.dto.ChallengeResponseDTO;
import com.hobby.challenge.fobackend.dto.CreateChallengeRequestDTO;
import com.hobby.challenge.fobackend.entity.Challenge;
import com.hobby.challenge.fobackend.service.ChallengeService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/challenges")
@RequiredArgsConstructor
public class ChallengeController {
	
	private final ChallengeService challengeService;
	
	@PostMapping
	public ResponseEntity<ChallengeResponseDTO> createChallenge(@Valid @RequestBody CreateChallengeRequestDTO dto,
			@AuthenticationPrincipal(expression = "userId") Integer userId){ // JWT에서 세팅한 사용자 아이디 꺼냄
			
		Challenge created = challengeService.createChallenge(dto, userId);
		
	    // 엔티티 -> ResponseDTO로 변환
	    ChallengeResponseDTO response = ChallengeResponseDTO.builder()
	            .challengeId(created.getChallengeId())
	            .title(created.getTitle())
	            .description(created.getDescription())
	            .startDate(created.getStartDate())
	            .endDate(created.getEndDate())
	            .categoryId(created.getCategoryId())
	            .createdBy(created.getCreatedBy())
	            .createdAt(created.getCreatedAt())
	            .build();

	    
		return ResponseEntity.ok(response);
	}
	
}
