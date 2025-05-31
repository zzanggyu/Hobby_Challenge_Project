package com.hobby.challenge.fobackend.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponseDTO {
    private Integer userId;
    private String loginId;
    private String username;
    private String nickname;
    private String email;
    private LocalDate birthDate;
    private String imageUrl;
    private String role;
    private Integer points;
    private Short level;
    private LocalDateTime createdDate;
	
}
