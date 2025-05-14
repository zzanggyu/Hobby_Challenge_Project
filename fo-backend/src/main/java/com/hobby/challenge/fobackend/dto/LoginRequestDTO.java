package com.hobby.challenge.fobackend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequestDTO {
    @NotBlank
    private String loginId;

    @NotBlank
    private String password;
}
