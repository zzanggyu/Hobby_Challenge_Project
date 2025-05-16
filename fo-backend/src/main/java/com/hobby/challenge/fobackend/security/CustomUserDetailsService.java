package com.hobby.challenge.fobackend.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.hobby.challenge.fobackend.entity.User;
import com.hobby.challenge.fobackend.mapper.UserMapper;

import lombok.RequiredArgsConstructor;

// 로그인 시 Spring Security가 사용자 정보를 가져올 때 사용하는 서비스. loginId로 DB에서 User 조회 후 UserDetails로 변환
@Service
@RequiredArgsConstructor // final 필드( ex:userMapper)를 인자로 받는 생성자 롬복이 자동 생성
public class CustomUserDetailsService implements UserDetailsService {
	private final UserMapper userMapper;

	@Override
	public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {
		User user = userMapper.findByLoginId(loginId); // key : loginId 로 UERT 테이블에서 사용자 조회
		if (user == null) {
			throw new UsernameNotFoundException("사용자를 찾을 수 없습니다.: " + loginId); // 401
		}
		return org.springframework.security.core.userdetails.User.builder()
				.username(user.getLoginId()) // principal(인증된 사용자) 아이디
				.password(user.getPassword()) // credentials 해시화된 비밀번호
				.roles(user.getRole()) // 권한(role) 설정
				.build();
	}

}
