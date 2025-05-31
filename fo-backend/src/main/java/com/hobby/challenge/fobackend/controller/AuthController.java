package com.hobby.challenge.fobackend.controller;

import java.util.Arrays;

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

import com.hobby.challenge.fobackend.dto.FindIdRequestDTO;
import com.hobby.challenge.fobackend.dto.FindIdResponseDTO;
import com.hobby.challenge.fobackend.dto.LoginRequestDTO;
import com.hobby.challenge.fobackend.dto.LoginResponseDTO;
import com.hobby.challenge.fobackend.dto.ResetPasswordRequestDTO;
import com.hobby.challenge.fobackend.dto.SendPasswordCodeRequestDTO;
import com.hobby.challenge.fobackend.dto.SignupRequestDTO;
import com.hobby.challenge.fobackend.dto.SignupResponseDTO;
import com.hobby.challenge.fobackend.dto.UserResponseDTO;
import com.hobby.challenge.fobackend.dto.VerifyCodeRequestDTO;
import com.hobby.challenge.fobackend.exception.CustomException;
import com.hobby.challenge.fobackend.exception.ErrorCode;
import com.hobby.challenge.fobackend.service.AuthService;
import com.hobby.challenge.fobackend.service.EmailAuthService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
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
	
    // 회원가입용 이메일 인증 코드 발송
    @PostMapping("/signup/send-code")
    public ResponseEntity<Void> sendSignupCode(
            @RequestParam("email") @Email String email) {
        emailAuthService.sendVerificationCode("signup", email);
        return ResponseEntity.ok().build();
    }
    
    // 회원가입용 이메일 인증 코드 검증
    @PostMapping("/signup/verify-code")
    public ResponseEntity<Void> verifySignupCode(
            @RequestParam("email") @Email String email,
            @RequestParam("code") @NotBlank String code) {
        boolean ok = emailAuthService.verifyCode("signup", email, code);
        if (!ok) throw new CustomException(ErrorCode.INVALID_CREDENTIALS);
        return ResponseEntity.ok().build();
    }
	
	//로그인
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(
            @Valid @RequestBody LoginRequestDTO dto,
            HttpServletResponse response) {
        LoginResponseDTO body = authService.login(dto, response);
        return ResponseEntity.ok(body);
    }
	
	// 리프레시 토큰
	@PostMapping("/refresh")
    public ResponseEntity<Void> refresh(HttpServletRequest req, HttpServletResponse res) {
        String refreshToken = Arrays.stream(req.getCookies())
            .filter(c -> "refreshToken".equals(c.getName()))
            .findFirst()
            .map(Cookie::getValue)
            .orElseThrow(() -> new CustomException(ErrorCode.REFRESH_TOKEN_MISSING));

        authService.refreshToken(refreshToken, res);
        return ResponseEntity.ok().build();
    }
	
	// 로그아웃
	@PostMapping("/logout")
	public ResponseEntity<Void> logout(HttpServletRequest req, HttpServletResponse res) {
	    String refreshToken = Arrays.stream(req.getCookies())
	        .filter(c -> "refreshToken".equals(c.getName()))
	        .map(Cookie::getValue)
	        .findFirst()
	        .orElseThrow(() -> new CustomException(ErrorCode.REFRESH_TOKEN_MISSING));
	    authService.logout(refreshToken, res);
	    return ResponseEntity.ok().build();
	}
	
    // 1) 인증 코드 발송 (아이디+이메일 확인)
    @PostMapping("/password/send-code")
    public ResponseEntity<Void> sendPasswordResetCode(
            @Valid @RequestBody SendPasswordCodeRequestDTO dto) {
        authService.sendPasswordResetCode(dto.getLoginId(), dto.getEmail());
        return ResponseEntity.ok().build();
    }

    // 2) 인증 코드 확인
    @PostMapping("/password/verify-code")
    public ResponseEntity<Void> verifyPasswordResetCode(
            @Valid @RequestBody VerifyCodeRequestDTO dto) {
        authService.verifyPasswordResetCode(dto.getEmail(), dto.getCode());
        return ResponseEntity.ok().build();
    }

    // 3) 새 비밀번호 저장
    @PostMapping("/password/reset")
    public ResponseEntity<Void> resetPassword(
            @Valid @RequestBody ResetPasswordRequestDTO dto) {
        authService.resetPassword(dto.getLoginId(), dto.getNewPassword());
        return ResponseEntity.ok().build();
    }
    
    // 아이디 찾기
    @PostMapping("/find-id")
    public ResponseEntity<FindIdResponseDTO> findId(
            @Valid @RequestBody FindIdRequestDTO dto) {
        String loginId = authService.findLoginIdByEmail(dto.getEmail(), dto.getUsername());
        return ResponseEntity.ok(new FindIdResponseDTO(loginId));
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