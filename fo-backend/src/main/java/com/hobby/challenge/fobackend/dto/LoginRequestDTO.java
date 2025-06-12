package com.hobby.challenge.fobackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // @Getter + @Setter / @RequireArgsConstructor / @ToString / @EqualsAndHashCode의 역할을 다 생성
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginRequestDTO {
    @NotBlank(message = "아이디를 입력하세요.")
    @Size(min = 6, max = 20, message = "아이디는6~20자여야 합니다.")
    @Pattern(
      regexp = "^[A-Za-z0-9]+$",
      message = "아이디는 영문(대소문자)과 숫자만 사용할 수 있습니다."
    )
    private String loginId;


    @NotBlank(message = "비밀번호를 입력하세요.")
    @Size(min = 8, max = 20, message = "비밀번호는 8~20자 사이여야 합니다.")
    @Pattern(
            regexp = "^[A-Za-z0-9!@#$%^&*]+$",
            message = "비밀번호: 영문, 숫자, 특수문자만 사용 가능합니다."
        )
    @Pattern(
      regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*]).+$",
      message = "비밀번호는 영문·숫자·특수문자를 각각 최소 1자 이상 포함해야 합니다."
    )
    private String password;
}
