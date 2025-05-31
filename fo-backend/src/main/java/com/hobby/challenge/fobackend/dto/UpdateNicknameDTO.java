package com.hobby.challenge.fobackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateNicknameDTO {
    @NotBlank(message = "닉네임을 입력하세요.")
    @Size(min = 2, max = 10, message = "닉네임은 2~10자여야 합니다.")
    @Pattern(regexp = "^[가-힣a-zA-Z0-9_]+$", 
             message = "닉네임은 한글, 영문, 숫자, 언더바만 사용 가능합니다.")
    private String nickname; // 변경할 닉네임
}
