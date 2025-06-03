package com.hobby.challenge.fobackend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hobby.challenge.fobackend.dto.ChallengeDetailDTO;
import com.hobby.challenge.fobackend.dto.ChallengeResponseDTO;
import com.hobby.challenge.fobackend.dto.CreateChallengeRequestDTO;
import com.hobby.challenge.fobackend.dto.PageResponseDTO;
import com.hobby.challenge.fobackend.dto.ParticipantDTO;
import com.hobby.challenge.fobackend.dto.UpdateChallengeRequestDTO;
import com.hobby.challenge.fobackend.service.ChallengeService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/challenges")
@RequiredArgsConstructor
public class ChallengeController {
	
	private final ChallengeService challengeService;
	
	// 챌린지 생성
	@PostMapping
	public ResponseEntity<ChallengeResponseDTO> createChallenge(@Valid @RequestBody CreateChallengeRequestDTO dto,
			@AuthenticationPrincipal(expression = "userId", errorOnInvalidType = false) Integer userId){ // JWT에서 세팅한 사용자 아이디 꺼냄
			
	    ChallengeResponseDTO created = challengeService.createChallenge(dto, userId);
	    return ResponseEntity.ok(created);
	}
	
	// 30개씩 페이징한 챌린지 목록 조회
	@GetMapping 
	public PageResponseDTO<ChallengeResponseDTO> listAll(
	    @RequestParam(name ="page", defaultValue = "1") int page,
	    @RequestParam(name ="size", defaultValue = "30") int size,
	    @RequestParam(name="search", required=false) String search,
	    @RequestParam(name="categoryId", required=false) Integer categoryId,
	    @AuthenticationPrincipal(expression = "userId", errorOnInvalidType = false) Integer userId
	) {
	    int offset = (page - 1) * size;
//	    if (userId == null) userId = -1;
	    return challengeService.getPageChallenges(userId, search, categoryId, size, offset);
	}
	
	// 전체 챌린지 목록 조회
//	@GetMapping
//    public ResponseEntity<List<ChallengeResponseDTO>> listAll() {
//        List<ChallengeResponseDTO> list = challengeService.getAllChallenges();
//        return ResponseEntity.ok(list);
//    }
	

    /** 참여한 챌린지 상세 조회 */
	@GetMapping("/{id:\\d+}")
	public ChallengeDetailDTO getDetail(
	    @PathVariable("id") Integer id,
	    @AuthenticationPrincipal(expression="userId", errorOnInvalidType = false) Integer userId
	) {
	    return challengeService.getChallengeDetail(id, userId);
	}
	
	// 인기 챌린지 조회 메인화면
	@GetMapping("/popular")
	public ResponseEntity<List<ChallengeResponseDTO>> getPopularChallenges(
	        @AuthenticationPrincipal(expression = "userId", errorOnInvalidType = false) Integer userId,
	        @RequestParam(name = "size", defaultValue = "12") int size) {
//	    Integer userId = null;
	    // 로그인하지 않은 사용자는 userId가 null일 수 있음
	    List<ChallengeResponseDTO> popular = challengeService.getPopularChallenges(userId, size);
	    return ResponseEntity.ok(popular);
	}
	
//	/** 승인된 참여자 목록 조회 */
//	@GetMapping("/{id}/participants")
//	public List<ParticipantDTO> getParticipants(
//	    @PathVariable("id") Integer id
//	) {
//	    return challengeService.getApprovedParticipants(id);
//	}
	
	// 챌린지 수정  챌린지 생성자(owner)만 가능
	@PutMapping("/{id}")
	public ChallengeResponseDTO updateChallenge(
	        @PathVariable("id") Integer id,
	        @Valid @RequestBody UpdateChallengeRequestDTO dto,
	        @AuthenticationPrincipal(expression = "userId", errorOnInvalidType = false) Integer userId) {
	    return challengeService.updateChallenge(id, dto, userId);
	}


	// 챌린지 삭제 챌린지 생성자(owner)만 가능
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteChallenge(
			 @PathVariable("id") Integer id,
			 @AuthenticationPrincipal(expression = "userId", errorOnInvalidType = false) Integer userId) {

	    challengeService.deleteChallenge(id, userId);
	    return ResponseEntity.noContent().build();
	}
	
	
	
}
