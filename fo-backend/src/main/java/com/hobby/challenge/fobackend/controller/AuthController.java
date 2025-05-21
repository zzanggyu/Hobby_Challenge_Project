package com.hobby.challenge.fobackend.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hobby.challenge.fobackend.dto.LoginRequestDTO;
import com.hobby.challenge.fobackend.dto.LoginResponseDTO;
import com.hobby.challenge.fobackend.dto.SignupRequestDTO;
import com.hobby.challenge.fobackend.dto.SignupResponseDTO;
import com.hobby.challenge.fobackend.dto.UserResponseDTO;
import com.hobby.challenge.fobackend.service.AuthService;
import com.hobby.challenge.fobackend.service.EmailAuthService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
	private final AuthService authService;
	private final EmailAuthService emailAuthService;
	@Value("${jwt.expiration}")
    private long jwtExpirationMs;
	
	// 회원가입
	@PostMapping("/signup")
	public ResponseEntity<SignupResponseDTO> signup(@Valid @RequestBody SignupRequestDTO dto){
		SignupResponseDTO created = authService.register(dto);
		// 201 Created + 빈 본문
		return ResponseEntity.status(HttpStatus.CREATED).body(created);
	}
	
	// 이메일로 코드 전송
    @PostMapping("/sendCode")
    public ResponseEntity<Void> sendCode(@RequestParam("email") @NotBlank @Email String email) {
        emailAuthService.sendVerificationCode(email);
        return ResponseEntity.ok().build();
    }
    
    // 이메일 인증코드 확인
    @PostMapping("/verifyCode")
    public ResponseEntity<Void> verifyCode(@RequestParam("email") @NotBlank @Email String email, 
    		@RequestParam("code") @NotBlank String code){
    	boolean ok = emailAuthService.verifyCode(email, code);
    	return ok ? ResponseEntity.ok().build()
    			: ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
	
	//로그인
	@PostMapping("/login")
	public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO dto, HttpServletResponse response){

		LoginResponseDTO body = authService.login(dto, response);
        return ResponseEntity.ok(body);
	}
	
	// 로그아웃
	@PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
		authService.logout(response);

        return ResponseEntity.ok().build();
    }
	
	// 새로고침 시 현재 인증된 사용자 정보를 반환
    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> me(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }
        String loginId = authentication.getName();
        UserResponseDTO dto = authService.getCurrentUser(loginId);
        return ResponseEntity.ok(dto);

    }
}