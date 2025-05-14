package com.hobby.challenge.fobackend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hobby.challenge.fobackend.entity.User;
import com.hobby.challenge.fobackend.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class UserController {
	private final UserService userService;
	
	// 특정 Id의 사용자 조회
	@GetMapping("/{userId}")
	public ResponseEntity<User> getUserById(@PathVariable("userId") Integer userId){
		User user = userService.getUserById(userId);
		if (user == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(user);
	}
	
	// 로그인 아이디로 사용자 조회(로그인 아이디 중복 검사)
	@GetMapping
	public ResponseEntity<User> getUserLogin(@RequestParam("loginId") String loginId){
		User user = userService.getUserByLoginId(loginId);
		if(user == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(user);
	}
}
