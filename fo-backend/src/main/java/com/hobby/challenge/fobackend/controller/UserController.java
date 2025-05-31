package com.hobby.challenge.fobackend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hobby.challenge.fobackend.dto.ChangePasswordDTO;
import com.hobby.challenge.fobackend.dto.DeleteAccountDTO;
import com.hobby.challenge.fobackend.dto.UpdateNicknameDTO;
import com.hobby.challenge.fobackend.dto.UserResponseDTO;
import com.hobby.challenge.fobackend.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    
    // 내 정보 조회 (로그인 필요)
    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getMyInfo(
        @AuthenticationPrincipal(expression="userId") Integer userId
    ) {
        return ResponseEntity.ok(userService.getUserInfo(userId));
    }
    
    // 닉네임 변경
    @PatchMapping("/me/nickname")
    public ResponseEntity<UserResponseDTO> updateNickname(
        @AuthenticationPrincipal(expression="userId") Integer userId,
        @Valid @RequestBody UpdateNicknameDTO dto
    ) {
        return ResponseEntity.ok(userService.updateNickname(userId, dto.getNickname()));
    }
    
    // 비밀번호 변경
    @PutMapping("/me/password")
    public ResponseEntity<Void> changePassword(
        @AuthenticationPrincipal(expression="userId") Integer userId,
        @Valid @RequestBody ChangePasswordDTO dto
    ) {
        userService.changePassword(userId, dto);
        return ResponseEntity.ok().build();
    }
    
    // 회원 탈퇴
    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteAccount(
        @AuthenticationPrincipal(expression="userId") Integer userId,
        @Valid @RequestBody DeleteAccountDTO dto
    ) {
        userService.deleteAccount(userId, dto.getPassword());
        return ResponseEntity.noContent().build();
    }
}