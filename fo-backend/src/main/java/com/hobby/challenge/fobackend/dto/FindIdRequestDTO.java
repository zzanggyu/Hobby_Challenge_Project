package com.hobby.challenge.fobackend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FindIdRequestDTO {
    @NotBlank @Email
    private String email;
    
    @NotBlank(message = "이름을 입력하세요.")
    private String username; // 이름 필드 추가
}
