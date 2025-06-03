package com.hobby.challenge.fobackend.dto;


import jakarta.validation.constraints.Email;
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
public class SignupRequestDTO {
  

    // 아이디: 영문·숫자 6~20자
    @NotBlank(message = "아이디를 입력하세요.")
    @Size(min = 6, max = 20, message = "아이디는 6~20자여야 합니다.")
    @Pattern(
      regexp = "^[A-Za-z0-9]+$",
      message = "아이디는 영문(대소문자)과 숫자만 사용할 수 있습니다."
    )
    private String loginId;

    // 비밀번호: 8~20자, 영문·숫자·특수문자 각 1자 이상
    @NotBlank(message = "비밀번호를 입력하세요.")
    @Size(min = 8, max = 20, message = "비밀번호는 8~20자 사이여야 합니다.")
    @Pattern(
      regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*]).+$",
      message = "비밀번호는 영문·숫자·특수문자를 각각 최소 1자 이상 포함해야 합니다."
    )
    private String password;

    // 이메일: 일반 형식 + 길이 제한
    @NotBlank(message = "이메일을 입력하세요.")
    @Email(message = "유효한 이메일 형식이 아닙니다.")
    @Size(max = 100, message = "이메일은 최대 100자까지 입력할 수 있습니다.")
    private String email;

    // 이름: 최대 5자(프론트 placeholder 기준)
    @NotBlank(message = "이름을 입력하세요.")
    @Size(max = 5, message = "이름은 최대 5자까지 입력할 수 있습니다.")
    private String username;

    // 닉네임(선택): 2~10자 (입력 안 하면 username 그대로 저장 로직 필요)
    @NotBlank(message = "닉네임을 입력하세요.")
    @Size(min = 2, max = 10, message = "닉네임은 2~10자여야 합니다.")
    @Pattern(
      regexp = "^$|.{2,10}$",
      message = "별명은 2~10자여야 합니다. 입력하지 않으면 이름이 닉네임으로 저장됩니다."
    )
    private String nickname;

    // 생년월일: YYYY-MM-DD 형식
    @NotBlank(message = "생년월일을 입력하세요.")
    @Pattern(
      regexp = "^\\d{4}\\.\\d{2}\\.\\d{2}$",
      message = "생년월일은 YYYY.MM.DD 형식이어야 합니다."
    )
    private String birthDate;
}