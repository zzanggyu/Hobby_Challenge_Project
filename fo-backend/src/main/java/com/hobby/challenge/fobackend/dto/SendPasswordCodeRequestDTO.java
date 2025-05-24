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
public class SendPasswordCodeRequestDTO {
    @NotBlank private String loginId;
    @NotBlank @Email private String email;
}
