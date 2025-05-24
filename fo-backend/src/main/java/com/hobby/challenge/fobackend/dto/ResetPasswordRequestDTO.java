package com.hobby.challenge.fobackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResetPasswordRequestDTO {
    @NotBlank private String loginId;
    
    // 비밀번호: 8~20자, 영문·숫자·특수문자 각 1자 이상
    @NotBlank(message = "비밀번호를 입력하세요.")
    @Size(min = 8, max = 20, message = "비밀번호는 8~20자 사이여야 합니다.")
    @Pattern(
      regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*]).+$",
      message = "비밀번호는 영문·숫자·특수문자를 각각 최소 1자 이상 포함해야 합니다."
    )
    private String newPassword;
}

