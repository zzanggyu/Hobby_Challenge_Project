package com.hobby.challenge.fobackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponseDTO {
    private Integer userId;
    private String username;
    // 나중에 토큰 추가
}
