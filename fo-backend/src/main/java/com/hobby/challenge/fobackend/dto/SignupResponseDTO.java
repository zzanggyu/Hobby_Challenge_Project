package com.hobby.challenge.fobackend.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SignupResponseDTO {
    private Integer userId;
    private String nickname;        // 프론트에서 바로 보여줄 닉네임
    private LocalDateTime createdDate;
}
