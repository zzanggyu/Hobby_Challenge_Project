package com.hobby.challenge.fobackend.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SignupRequestDTO {
  
    @NotBlank @Size(min = 8, max = 20)
    private String loginId;

    @NotBlank @Size(min = 8, max = 20)
    private String password;

    @NotBlank @Email
    private String email;

    @NotBlank @Size(max = 50)
    private String username;

    private String nickname;
    private String birthDate;   // "yyyy-MM-dd"
    private String gender;      // "MALE" or "FEMALE"
}
