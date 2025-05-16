package com.hobby.challenge.fobackend.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hobby.challenge.fobackend.dto.UserResponseDTO;
import com.hobby.challenge.fobackend.entity.User;
import com.hobby.challenge.fobackend.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
	private final UserService userService;
	
	// 특정 Id의 사용자 조회
	@GetMapping("/{userId}")
	public ResponseEntity<UserResponseDTO> getUserById(@PathVariable("userId") Integer userId){
		User user = userService.getUserById(userId);
		if (user == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(toDto(user));
	}
	
	// 로그인 아이디로 사용자 조회(로그인 아이디 중복 검사)
	@GetMapping("login/{loginId}")
	public ResponseEntity<UserResponseDTO> getUserLogin(@RequestParam("loginId") String loginId){
		User user = userService.getUserByLoginId(loginId);
		if(user == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(toDto(user));
	}
	
	
	// JWT 테스트 내 프로필 조회
    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getMyProfile(
        @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal
    ) {
        // principal.getUsername() == loginId
        User user = userService.getUserByLoginId(principal.getUsername());
        return ResponseEntity.ok(toDto(user));
    }
    

    // 엔티티 → DTO 변환 헬퍼 
    private UserResponseDTO toDto(User u) {
        return new UserResponseDTO(
            u.getUserId(),
            u.getLoginId(),
            u.getUsername(),
            u.getNickname(),
            u.getEmail(),
            u.getBirthDate(),
            u.getImageUrl(),
            u.getRole(),     
            u.getPoints(),
            u.getLevel(),
            u.getCreatedDate().toLocalDateTime()       // LocalDateTime 인 경우
        );
    }
}
