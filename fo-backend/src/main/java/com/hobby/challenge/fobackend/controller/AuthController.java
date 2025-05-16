package com.hobby.challenge.fobackend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hobby.challenge.fobackend.dto.LoginRequestDTO;
import com.hobby.challenge.fobackend.dto.LoginResponseDTO;
import com.hobby.challenge.fobackend.dto.SignupRequestDTO;
import com.hobby.challenge.fobackend.entity.User;
import com.hobby.challenge.fobackend.mapper.UserMapper;
import com.hobby.challenge.fobackend.security.JwtTokenProvider;
import com.hobby.challenge.fobackend.service.EmailAuthService;
import com.hobby.challenge.fobackend.service.UserService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
	private final UserService userService;
	private final EmailAuthService emailAuthService;
	private final AuthenticationManager authManager;
	private final JwtTokenProvider tokenProvider;
	private final UserMapper userMapper;
	
	// 회원가입
	@PostMapping("/signup")
	public ResponseEntity<Void> signup(@Valid @RequestBody SignupRequestDTO dto){
		userService.register(dto);
		// 201 Created + 빈 본문
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}
	
	//로그인
	@PostMapping("/login")
	public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO req){
		// 아이디 비밀번호 검증
		Authentication authentication = authManager.authenticate(
				new UsernamePasswordAuthenticationToken(
						req.getLoginId(), 
						req.getPassword())
				);
		// SecurityContext에 인증 정보 저장
		SecurityContextHolder.getContext().setAuthentication(authentication);
		// JWT 토큰 생성
		String token = tokenProvider.createToken(req.getLoginId());
		// 추가 사용자 정보 조회
		User user = userMapper.findByLoginId(req.getLoginId());
		// 응답 LoginResponseDTO(userId,nickname,token)
		LoginResponseDTO response = new LoginResponseDTO(
				user.getUserId(),
				user.getNickname(),
				token
		);

        return ResponseEntity.ok(response);
	}
	
	// 이메일로 코드 전송
    @PostMapping("/sendcode")
    public ResponseEntity<Void> sendCode(@RequestParam("email") @NotBlank @Email String email) {
        emailAuthService.sendVerificationCode(email);
        return ResponseEntity.ok().build();
    }
    
    // 코드 확인
    @PostMapping("/verifycode")
    public ResponseEntity<Void> verifyCode(@RequestParam("email") @NotBlank @Email String email, 
    		@RequestParam("code") @NotBlank String code){
    	boolean ok = emailAuthService.verifyCode(email, code);
    	return ok ? ResponseEntity.ok().build()
    			: ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
}