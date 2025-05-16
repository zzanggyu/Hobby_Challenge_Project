package com.hobby.challenge.fobackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

// 로그인할 때 응답
@Data
@AllArgsConstructor
public class LoginResponseDTO {
    private Integer userId;
    private String nickname;
    // 나중에 토큰 추가
    private String token;
}
