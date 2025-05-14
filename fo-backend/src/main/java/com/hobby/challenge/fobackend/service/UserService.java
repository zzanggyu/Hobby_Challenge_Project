package com.hobby.challenge.fobackend.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.hobby.challenge.fobackend.dto.SignupRequestDTO;
import com.hobby.challenge.fobackend.entity.User;
import com.hobby.challenge.fobackend.mapper.UserMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor // final 필드를 인자로 받는 생성자 자동 생성
public class UserService {
	private final UserMapper userMapper;
	private final PasswordEncoder passwordEncoder;
	
	// 생성자 @Autowired를 사용해 명시적 생성자 주입도 가능
//	public UserService(UserMapper userMapper) {
//		this.userMapper = userMapper;
//	}

	public User getUserById(Integer id) {
		return userMapper.findById(id);
	}
	
	public User createUser(User user) {
		userMapper.insertUser(user);
		return user;
	}
	
    public User getUserByLoginId(String loginId) {
        return userMapper.findByLoginId(loginId);
    }
	
	// 회원가입
	public User register(SignupRequestDTO sr) {
		// 중복 확인
		if(getUserByLoginId(sr.getLoginId()) != null) {
			throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
		}
		// DTO -> Entity
		User user = User.builder()
				.loginId(sr.getLoginId())
				.password(passwordEncoder.encode(sr.getPassword()))
				.email(sr.getEmail())
				.username(sr.getUsername())
				.nickname(sr.getNickname())
				.birthDate(java.time.LocalDate.parse(sr.getBirthDate()))   
				.build();
		
		userMapper.insertUser(user);
		return user;
	}
	
    // 로그인 검증
    public User authenticate(String loginId, String rawPassword) {
        User u = getUserByLoginId(loginId);
        if (u == null || !passwordEncoder.matches(rawPassword, u.getPassword())) {
            return null;
        }
        return u;
    }
}
