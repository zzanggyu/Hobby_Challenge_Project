package com.hobby.challenge.fobackend.service.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.hobby.challenge.fobackend.dto.SignupRequestDTO;
import com.hobby.challenge.fobackend.entity.User;
import com.hobby.challenge.fobackend.exception.CustomException;
import com.hobby.challenge.fobackend.exception.ErrorCode;
import com.hobby.challenge.fobackend.mapper.UserMapper;
import com.hobby.challenge.fobackend.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor // final 필드를 인자로 받는 생성자 자동 생성
public class UserServiceImpl implements UserService{
	private final UserMapper userMapper;
	private final PasswordEncoder passwordEncoder; // Bcrypt 암호화 인코더 (수정)
	
	// 생성자 @Autowired를 사용해 명시적 생성자 주입도 가능
//	public UserService(UserMapper userMapper) {
//		this.userMapper = userMapper;
//	}
	
	// 사용자 식별 번호로 사용자 조회
	public User getUserById(Integer id) {
		return userMapper.findById(id);
	}
	
	// 로그인 아이디로 사용자 조회
    public User getUserByLoginId(String loginId) {
        return userMapper.findByLoginId(loginId);
    }
	
	// 회원가입
	public User register(SignupRequestDTO sr) {
		// 아이디 중복 확인
		if(getUserByLoginId(sr.getLoginId()) != null) {
			throw new CustomException(ErrorCode.DUPLICATE_LOGINID);
		}
		// 이메일 중복 확인
		if (userMapper.findByEmail(sr.getEmail()) != null) {
		    throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
		}
        //  닉네임 기본값 처리
        String nickname = sr.getNickname();
        if (nickname == null || nickname.isBlank()) {
            nickname = sr.getUsername();
        }
		
		// DTO -> Entity 사용자 엔티티 생성
		User user = User.builder()
				.loginId(sr.getLoginId())
				.password(passwordEncoder.encode(sr.getPassword()))
				.email(sr.getEmail())
				.username(sr.getUsername())
				.nickname(nickname) // 닉네임 설정했을 때 
				.birthDate(java.time.LocalDate.parse(sr.getBirthDate()))   
				.build();
		
		userMapper.insertUser(user); // DB에 저장
		return user;
	}
	
    // 로그인 검증
    public User authenticate(String loginId, String rawPassword) {
        User user = getUserByLoginId(loginId); // 로그인 아이디로 사용자 정보 조회
        // 사용자 존재 여부 비밀번호 맞는지 확인
        if (user == null || !passwordEncoder.matches(rawPassword, user.getPassword())) {
            return null;
        }
        return user;
    }
}
