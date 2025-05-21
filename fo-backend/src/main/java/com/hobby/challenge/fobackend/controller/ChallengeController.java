package com.hobby.challenge.fobackend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hobby.challenge.fobackend.dto.ChallengeResponseDTO;
import com.hobby.challenge.fobackend.dto.CreateChallengeRequestDTO;
import com.hobby.challenge.fobackend.service.ChallengeService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/challenges")
@RequiredArgsConstructor
public class ChallengeController {
	
	private final ChallengeService challengeService;
	
	// 전체 챌린지 목록 조회
	@GetMapping
    public ResponseEntity<List<ChallengeResponseDTO>> listAll() {
        List<ChallengeResponseDTO> list = challengeService.getAllChallenges();
        return ResponseEntity.ok(list);
    }
	
	// 챌린지 생성
	@PostMapping
	public ResponseEntity<ChallengeResponseDTO> createChallenge(@Valid @RequestBody CreateChallengeRequestDTO dto,
			@AuthenticationPrincipal(expression = "userId") Integer userId){ // JWT에서 세팅한 사용자 아이디 꺼냄
			
	    ChallengeResponseDTO created = challengeService.createChallenge(dto, userId);
	    return ResponseEntity.ok(created);
	}
	

	
}
