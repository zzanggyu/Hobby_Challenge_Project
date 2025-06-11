package com.hobby.challenge.fobackend.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
import com.hobby.challenge.fobackend.service.EmailAuthService;
import com.hobby.challenge.fobackend.service.LoginHistoryService;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor // final 필드를 인자로 받는 생성자 자동 생성
public class AuthServiceImpl implements AuthService{
	private final AuthenticationManager authManager;
	private final AuthMapper authMapper;
	private final PasswordEncoder passwordEncoder; // Bcrypt 암호화 인코더 (수정)
	private final JwtTokenProvider tokenProvider;
	private final LoginHistoryService loginHistoryService;
	private final StringRedisTemplate redisTemplate;   // RedisTemplate
	private final EmailAuthService emailAuthService;
	@Value("${jwt.expiration}")
	private long jwtExpirationMs;  // 엑세스 토큰 만료 시간
    @Value("${jwt.refreshExpiration}")
    private long jwtRefreshExpirationMs; // 리프레시 토큰 만료시간
	
	// 생성자 @Autowired를 사용해 명시적 생성자 주입도 가능
//	public UserService(UserMapper userMapper) {
//		this.userMapper = userMapper;
//	}
	
	
	// 회원가입
	public SignupResponseDTO register(SignupRequestDTO dto) {
		// 아이디 중복 확인
		if(authMapper.findByLoginId(dto.getLoginId()) != null) {
			throw new CustomException(ErrorCode.DUPLICATE_LOGINID);
		}
		// 이메일 중복 확인
		if (authMapper.findByEmail(dto.getEmail()) != null) {
		    throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
		}
		
		// 닉네임 중복 확인
	    if (authMapper.findByNickname(dto.getNickname()) != null) {
	        throw new CustomException(ErrorCode.DUPLICATE_NICKNAME);
	    }
        
        // DTO의 birthDate(String) → LocalDate로 파싱
        LocalDate birth = LocalDate.parse(dto.getBirthDate(), DateTimeFormatter.ofPattern("yyyy.MM.dd"));
        if (birth.isAfter(LocalDate.now())) {
            throw new CustomException(ErrorCode.INVALID_BIRTHDATE);
        }
        
		// DTO -> Entity 사용자 엔티티 생성
		User user = User.builder()
				.loginId(dto.getLoginId())
				.password(passwordEncoder.encode(dto.getPassword()))
				.email(dto.getEmail())
				.username(dto.getUsername())
				.nickname(dto.getNickname()) // 닉네임 설정했을 때 
				.birthDate(birth)   
				.build();
		
		authMapper.insertUser(user); // DB에 저장
		

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
    	// 회원 정보 찾기 db에서
        User user = authMapper.findByLoginId(dto.getLoginId());
        if (user == null) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND, "존재하지 않는 계정입니다. 회원가입 하세요!");
        }
        
        // 사용자 상태 확인
        if ("STOP".equals(user.getStatus())) {
            throw new CustomException(ErrorCode.USER_SUSPENDED, "정지된 계정입니다. 관리자에게 문의하세요.");
        }
        
        // 인증 
        Authentication auth = authManager.authenticate(
            new UsernamePasswordAuthenticationToken(dto.getLoginId(), dto.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(auth);

        // 엑세스 토큰 생성 + 쿠키 세팅
        String accessToken = tokenProvider.createToken(dto.getLoginId());
        ResponseCookie cookie = ResponseCookie.from("token", accessToken)
            .httpOnly(true)
            .secure(false)  // HTTPS 아닌 로컬에선 이 옵션 때문에 브라우저가 보내지 않음 ,개발 중에는 false, 운영(HTTPS) 시 true
            .path("/")
            .maxAge(jwtExpirationMs/1000)
            .sameSite("Lax") // cross-site 허용 'strict'가 더 센 보안 CSRF 공격 방지 
            .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        
        // Refresh Token 생성
        String refreshToken = tokenProvider.createRefreshToken(dto.getLoginId());
        // Redis에 (key=refresh:{token}, value=loginId) TTL 설정 (Time To Live)
        redisTemplate.opsForValue()
                     .set("refresh:" + refreshToken, dto.getLoginId(),
                          jwtRefreshExpirationMs, TimeUnit.MILLISECONDS);
        // 클라이언트에 쿠키로도 전달
        response.addHeader(HttpHeaders.SET_COOKIE,
            ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(false) // 개발 중에는 false, 운영(HTTPS) 시 true
                .path("/") // 루트로 설정해야 로그아웃 시에도 브라우저가 쿠키를 전달하고 삭제 헤더가 적용
                .maxAge(jwtRefreshExpirationMs/1000)
                .sameSite("Lax")
                .build()
                .toString());

        //  로그인 이력
        loginHistoryService.recordLoginHistory(
            LoginHistoryDTO.builder()
                .userId(user.getUserId())
                .loginDate(LocalDateTime.now())
                .build()
        );
        
        // 응답 DTO
        LoginResponseDTO res = LoginResponseDTO.builder()
            .userId(user.getUserId())
            .nickname(user.getNickname())
            .token(accessToken)
            .build();
        
        return res;

    }
    
    // 리프레시 토큰
    @Override
    public void refreshToken(String refreshToken, HttpServletResponse response) {
        String loginId = redisTemplate.opsForValue().get("refresh:" + refreshToken);
        if (loginId == null) {
            throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
        }
        // 유효하면 새 Access Token 발급 + 쿠키 세팅
        String newAccess = tokenProvider.createToken(loginId);
        response.addHeader(HttpHeaders.SET_COOKIE,
            ResponseCookie.from("token", newAccess)
                .httpOnly(true)
                .secure(false) // 개발 중에는 false, 운영(HTTPS) 시 true
                .path("/")
                .maxAge(jwtExpirationMs/1000)
                .sameSite("Lax") // cross-site 허용
                .build()
                .toString());
    }
    
    // 로그아웃
    @Override
    public void logout(String refreshToken,HttpServletResponse response) {
        // (Controller에서 refreshToken 쿠키 값을 꺼내면)
        // redisTemplate.delete("refresh:" + refreshToken);
    	
        // Redis에서 해당 키 삭제
        redisTemplate.delete("refresh:" + refreshToken);
        
        // Access Token 쿠키 삭제
        response.addHeader(HttpHeaders.SET_COOKIE,
            ResponseCookie.from("token", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .sameSite("Lax").build()
                .toString());
        // Refresh Token 쿠키 삭제
        response.addHeader(HttpHeaders.SET_COOKIE,
            ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(false)
                .path("/").maxAge(0)
                .sameSite("Lax").build()
                .toString());
    }
    
    // 로그인한 사용자의 정보 가져오기
    @Override
    public UserResponseDTO getCurrentUser(String loginId) {
        User user = authMapper.findByLoginId(loginId);
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
    
    // 회원가입 이메일 인증
    @Override
    public void sendSignupEmailCode(String email) {
        emailAuthService.sendVerificationCode("signup", email);
    }

    // 회원가입 이메일 인증코드 확인
    @Override
    public void verifySignupEmailCode(String email, String code) {
        boolean ok = emailAuthService.verifyCode("signup", email, code);
        if (!ok) throw new CustomException(ErrorCode.INVALID_VERIFICATION_CODE);
    }

    // 비밀번호 재설정 인증
    @Override
    public void sendPasswordResetCode(String loginId, String email) {
        User u = authMapper.findByLoginId(loginId);
        if (u == null || !u.getEmail().equals(email)) {
            throw new CustomException(ErrorCode.INVALID_CREDENTIALS);
        }
        emailAuthService.sendVerificationCode("passwordreset", email);
    }
    
    // 비밀번호 재설정 코드 확인
    @Override
    public void verifyPasswordResetCode(String email, String code) {
        boolean ok = emailAuthService.verifyCode("passwordreset", email, code);
        if (!ok) throw new CustomException(ErrorCode.INVALID_VERIFICATION_CODE);
    }
    
    // 비밀번호 재설정
    @Override
    @Transactional
    public void resetPassword(String loginId, String newPassword) {
        String enc = passwordEncoder.encode(newPassword);
        authMapper.updatePassword(loginId, enc);
    }
    
    // 아이디 찾기
    @Override
    public String findLoginIdByEmail(String email, String username) {
        User u = authMapper.findByEmailAndUsername(email, username);
        if (u == null) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND, 
                "입력하신 정보와 일치하는 계정을 찾을 수 없습니다.");
        }
        return u.getLoginId();
    }


}