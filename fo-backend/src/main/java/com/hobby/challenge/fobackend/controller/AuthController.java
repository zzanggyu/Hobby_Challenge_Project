package com.hobby.challenge.fobackend.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
import com.hobby.challenge.fobackend.dto.UserResponseDTO;
import com.hobby.challenge.fobackend.entity.User;
import com.hobby.challenge.fobackend.mapper.UserMapper;
import com.hobby.challenge.fobackend.security.JwtTokenProvider;
import com.hobby.challenge.fobackend.service.EmailAuthService;
import com.hobby.challenge.fobackend.service.UserService;

import jakarta.servlet.http.Cookie;
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
	private final UserService userService;
	private final EmailAuthService emailAuthService;
	private final AuthenticationManager authManager;
	private final JwtTokenProvider tokenProvider;
	private final UserMapper userMapper;
	@Value("${jwt.expiration}")
    private long jwtExpirationMs;
	
	// 회원가입
	@PostMapping("/signup")
	public ResponseEntity<Void> signup(@Valid @RequestBody SignupRequestDTO dto){
		userService.register(dto);
		// 201 Created + 빈 본문
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}
	
	// 이메일로 코드 전송
    @PostMapping("/sendCode")
    public ResponseEntity<Void> sendCode(@RequestParam("email") @NotBlank @Email String email) {
        emailAuthService.sendVerificationCode(email);
        return ResponseEntity.ok().build();
    }
    
    // 코드 확인
    @PostMapping("/verifyCode")
    public ResponseEntity<Void> verifyCode(@RequestParam("email") @NotBlank @Email String email, 
    		@RequestParam("code") @NotBlank String code){
    	boolean ok = emailAuthService.verifyCode(email, code);
    	return ok ? ResponseEntity.ok().build()
    			: ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
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
		// 쿠키 생성
		ResponseCookie jwtCookie = ResponseCookie.from("token", token)
			    .httpOnly(true)                       // JavaScript 접근 불가
			    .secure(true)                         // HTTPS 에서만 전송
			    .path("/")                            // 모든 경로에서 전송
			    .maxAge(jwtExpirationMs / 1000) // 초 단위 만료시간
			    .sameSite("Strict")                   // CSRF 완화
			    .build();
		
		// 추가 사용자 정보 조회
		User user = userMapper.findByLoginId(req.getLoginId());
		// 응답 LoginResponseDTO(userId,nickname,token)
		LoginResponseDTO body = new LoginResponseDTO(
				user.getUserId(),
				user.getNickname(),
				token
		);
		// set-Cookie 헤더와 함꼐 응답
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .body(body);
	}
	
	// 로그아웃
	@PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        // access token 쿠키 삭제
        Cookie tokenCookie = new Cookie("token", null);
        tokenCookie.setHttpOnly(true);
        tokenCookie.setSecure(true);
        tokenCookie.setPath("/");
        tokenCookie.setMaxAge(0);
        response.addCookie(tokenCookie);

        // refresh token 쿠키도 사용 중이라면 함께 삭제
//        Cookie refreshCookie = new Cookie("refresh", null);
//        refreshCookie.setHttpOnly(true);
//        refreshCookie.setSecure(true);
//        refreshCookie.setPath("/");
//        refreshCookie.setMaxAge(0);
//        response.addCookie(refreshCookie);

        return ResponseEntity.ok().build();
    }
	
	// 현재 인증된 사용자의 정보를 반환
	@GetMapping("/me")
    public ResponseEntity<UserResponseDTO> me(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }
        String loginId = authentication.getName();
        User user = userService.getUserByLoginId(loginId);
        // 필요한 필드만 담아서 반환할 DTO
        UserResponseDTO dto = new UserResponseDTO(
        	    user.getUserId(),
        	    user.getLoginId(),
        	    user.getUsername(),
        	    user.getNickname(),
        	    user.getEmail(),
        	    user.getBirthDate(),
        	    user.getImageUrl(),
        	    user.getRole(),         
        	    user.getPoints(),
        	    user.getLevel(),
        	    user.getCreatedDate().toLocalDateTime() 
        	    
        );
        return ResponseEntity.ok(dto);
    }
	

}