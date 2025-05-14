package com.hobby.challenge.fobackend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hobby.challenge.fobackend.dto.LoginRequestDTO;
import com.hobby.challenge.fobackend.dto.LoginResponseDTO;
import com.hobby.challenge.fobackend.dto.SignupRequestDTO;
import com.hobby.challenge.fobackend.entity.User;
import com.hobby.challenge.fobackend.service.EmailAuthService;
import com.hobby.challenge.fobackend.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
	private final UserService userService;
	private final EmailAuthService emailAuthService;
	
	// 회원가입
	@PostMapping("/signup")
	public ResponseEntity<User> signup(@Valid @RequestBody SignupRequestDTO sr){
		User created = userService.register(sr);
		return ResponseEntity.ok(created);
	}
	
	//로그인
	@PostMapping("/login")
	public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO req){
		User user = userService.authenticate(req.getLoginId(), req.getPassword());
		if(user == null) {
			return ResponseEntity.status(401).build();
		}
        LoginResponseDTO response = new LoginResponseDTO(user.getUserId(), user.getUsername());
        return ResponseEntity.ok(response);
	}
	
	// 이메일로 코드 전송
    @PostMapping("/sendcode")
    public ResponseEntity<Void> sendCode(@RequestParam("email") String email) {
        emailAuthService.sendVerificationCode(email);
        return ResponseEntity.ok().build();
    }
    
    // 코드 확인
    @PostMapping("/verifycode")
    public ResponseEntity<Void> verify(@RequestParam("email") String email, @RequestParam("code") String code){
    	boolean ok = emailAuthService.verifyCode(email, code);
    	return ok ? ResponseEntity.ok().build()
    			: ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
}