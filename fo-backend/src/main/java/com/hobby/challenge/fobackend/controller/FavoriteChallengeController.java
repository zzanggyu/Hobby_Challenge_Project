package com.hobby.challenge.fobackend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hobby.challenge.fobackend.dto.FavoriteChallengeDTO;
import com.hobby.challenge.fobackend.service.FavoriteChallengeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/challenges/favorite")
@RequiredArgsConstructor
public class FavoriteChallengeController {
	private final FavoriteChallengeService favService;
	
	// 관심 챌린지 목록 가져오기
	@GetMapping
	public ResponseEntity<List<FavoriteChallengeDTO>> listMyFavorite(
			@AuthenticationPrincipal(expression = "userId", errorOnInvalidType = false) int userId){
		return ResponseEntity.ok(favService.getMyFavorites(userId));
	}
	

    // 토글: 이미 있으면 삭제, 없으면 추가
    @PostMapping("/{challengeId}")
    public ResponseEntity<Void> toggleFavorite(
    		@AuthenticationPrincipal(expression = "userId", errorOnInvalidType = false) int userId,
            @PathVariable("challengeId") int challengeId) {

        favService.toggleFavorite(userId, challengeId);
        return ResponseEntity.ok().build();
    }
	
}
