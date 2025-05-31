package com.hobby.challenge.fobackend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeleteAccountDTO {
    @NotBlank(message = "비밀번호를 입력하세요.")
    private String password; // 본인확인용 비번
}
