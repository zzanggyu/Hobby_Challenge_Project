package com.hobby.challenge.fobackend.controller;


import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hobby.challenge.fobackend.dto.ChangePasswordDTO;
import com.hobby.challenge.fobackend.dto.DeleteAccountDTO;
import com.hobby.challenge.fobackend.dto.UpdateNicknameDTO;
import com.hobby.challenge.fobackend.dto.UserResponseDTO;
import com.hobby.challenge.fobackend.service.CertificationService;
import com.hobby.challenge.fobackend.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final CertificationService certificationService;
    
    // 내 정보 조회 (로그인 필요)
    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getMyInfo(
    	@AuthenticationPrincipal(expression="userId", errorOnInvalidType = false) Integer userId) {
        return ResponseEntity.ok(userService.getUserInfo(userId));
    }
    
	// 특정 챌린지에서 특정 사용자의 인증 개수 조회
	@GetMapping("/{userId}/challenges/{challengeId}/certifications/count")
	public ResponseEntity<Map<String, Object>> getMyCertificationCount(
			@PathVariable("userId") Integer userId,
	        @PathVariable("challengeId") Integer challengeId) {
	    
	    try {
	        int count = certificationService.getCertificationCountByUserAndChallenge(challengeId, userId);
	        Map<String, Object> response = new HashMap<>();
	    	response.put("userId", userId);
	    	response.put("challengeId", challengeId);
	    	response.put("certificationCount", count);
	    	response.put("success", true);
	    	
	    	return ResponseEntity.ok(response);
	    } catch (Exception e) {
	    	Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "인증 개수 조회에 실패했습니다.");
            errorResponse.put("error", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                               .body(errorResponse);
	    }
	}
    
    // 닉네임 변경
    @PatchMapping("/me/nickname")
    public ResponseEntity<UserResponseDTO> updateNickname(
    	@AuthenticationPrincipal(expression="userId", errorOnInvalidType = false) Integer userId,
        @Valid @RequestBody UpdateNicknameDTO dto
    ) {
        return ResponseEntity.ok(userService.updateNickname(userId, dto.getNickname()));
    }
    
    // 비밀번호 변경
    @PutMapping("/me/password")
    public ResponseEntity<Void> changePassword(
    	@AuthenticationPrincipal(expression="userId", errorOnInvalidType = false) Integer userId,
        @Valid @RequestBody ChangePasswordDTO dto
    ) {
        userService.changePassword(userId, dto);
        return ResponseEntity.ok().build();
    }
    
    // 회원 탈퇴
    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteAccount(
    	@AuthenticationPrincipal(expression="userId", errorOnInvalidType = false) Integer userId,
        @Valid @RequestBody DeleteAccountDTO dto
    ) {
        userService.deleteAccount(userId, dto.getPassword());
        return ResponseEntity.noContent().build();
    }
}