package com.hobby.challenge.fobackend.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.hobby.challenge.fobackend.dto.LoginHistoryDTO;
import com.hobby.challenge.fobackend.dto.LoginRequestDTO;
import com.hobby.challenge.fobackend.dto.LoginResponseDTO;
import com.hobby.challenge.fobackend.dto.SignupRequestDTO;
import com.hobby.challenge.fobackend.dto.SignupResponseDTO;
import com.hobby.challenge.fobackend.dto.UserResponseDTO;
import com.hobby.challenge.fobackend.entity.User;
import com.hobby.challenge.fobackend.exception.CustomException;
import com.hobby.challenge.fobackend.exception.ErrorCode;
import com.hobby.challenge.fobackend.mapper.AuthMapper;
import com.hobby.challenge.fobackend.security.JwtTokenProvider;
import com.hobby.challenge.fobackend.service.AuthService;
import com.hobby.challenge.fobackend.service.LoginHistoryService;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor // final 필드를 인자로 받는 생성자 자동 생성
public class AuthServiceImpl implements AuthService{
	private final AuthenticationManager authManager;
	private final AuthMapper userMapper;
	private final PasswordEncoder passwordEncoder; // Bcrypt 암호화 인코더 (수정)
	private final JwtTokenProvider tokenProvider;
	private final LoginHistoryService loginHistoryService;
	@Value("${jwt.expiration}")
	private long jwtExpirationMs; 
	
	// 생성자 @Autowired를 사용해 명시적 생성자 주입도 가능
//	public UserService(UserMapper userMapper) {
//		this.userMapper = userMapper;
//	}
	
	
	// 회원가입
	public SignupResponseDTO register(SignupRequestDTO dto) {
		// 아이디 중복 확인
		if(userMapper.findByLoginId(dto.getLoginId()) != null) {
			throw new CustomException(ErrorCode.DUPLICATE_LOGINID);
		}
		// 이메일 중복 확인
		if (userMapper.findByEmail(dto.getEmail()) != null) {
		    throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
		}
        //  닉네임 기본값 처리
        String nickname = dto.getNickname();
        if (nickname == null || nickname.isBlank()) {
            nickname = dto.getUsername();
        }
        
        // 1) DTO의 birthDate(String) → LocalDate로 파싱
        LocalDate birth = LocalDate.parse(dto.getBirthDate(),
                DateTimeFormatter.ofPattern("yyyy.MM.dd"));
        
		// DTO -> Entity 사용자 엔티티 생성
		User user = User.builder()
				.loginId(dto.getLoginId())
				.password(passwordEncoder.encode(dto.getPassword()))
				.email(dto.getEmail())
				.username(dto.getUsername())
				.nickname(nickname) // 닉네임 설정했을 때 
				.birthDate(birth)   
				.build();
		
		userMapper.insertUser(user); // DB에 저장
		

        SignupResponseDTO response = SignupResponseDTO.builder()
        		.userId(user.getUserId())
        		.nickname(user.getNickname())
        		.createdDate(user.getCreatedDate())
        		.build();
        
        return response;
	}
	
    // 로그인 검증
    @Override
    public LoginResponseDTO login(LoginRequestDTO dto,  HttpServletResponse response) {
    	
        // 1) 인증
        Authentication auth = authManager.authenticate(
            new UsernamePasswordAuthenticationToken(dto.getLoginId(), dto.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(auth);

        // 2) JWT 생성 & 쿠키 세팅
        String token = tokenProvider.createToken(dto.getLoginId());
        ResponseCookie cookie = ResponseCookie.from("token", token)
            .httpOnly(true).secure(true).path("/")
            .maxAge(jwtExpirationMs/1000).sameSite("Strict").build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        // 3) 로그인 이력
        User user = userMapper.findByLoginId(dto.getLoginId());
        loginHistoryService.recordLoginHistory(
            LoginHistoryDTO.builder()
                .userId(user.getUserId())
                .loginDate(LocalDateTime.now())
                .build()
        );
        
        // 4) 응답 DTO
        LoginResponseDTO res = LoginResponseDTO.builder()
            .userId(user.getUserId())
            .nickname(user.getNickname())
            .token(token)
            .build();
        
        return res;

    }
    
    // 로그아웃
    @Override
    public void logout(HttpServletResponse response) {
        // 엑세스 토큰 쿠키 제거
        ResponseCookie cookie = ResponseCookie.from("token", "")
            .httpOnly(true).secure(true).path("/").maxAge(0).sameSite("Strict").build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
    
    @Override
    public UserResponseDTO getCurrentUser(String loginId) {
        User user = userMapper.findByLoginId(loginId);
        if (user == null) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }
        UserResponseDTO response = UserResponseDTO.builder()
            .userId(user.getUserId())
            .loginId(user.getLoginId())
            .username(user.getUsername())
            .nickname(user.getNickname())
            .email(user.getEmail())
            .birthDate(user.getBirthDate())
            .imageUrl(user.getImageUrl())
            .role(user.getRole())
            .points(user.getPoints())
            .level(user.getLevel())
            .createdDate(user.getCreatedDate())
            .build();
        
        return response;
    }
}