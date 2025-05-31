package com.hobby.challenge.fobackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordDTO {
    @NotBlank(message = "현재 비밀번호를 입력하세요.")
    private String currentPassword;  
    
    @NotBlank(message = "새 비밀번호를 입력하세요.")
    @Size(min = 8, max = 20, message = "비밀번호는 8~20자 사이여야 합니다.")
    @Pattern(
        regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*]).+$",
        message = "비밀번호는 영문·숫자·특수문자를 각각 최소 1자 이상 포함해야 합니다."
    )
    private String newPassword;  // 새 비밀번호
}

