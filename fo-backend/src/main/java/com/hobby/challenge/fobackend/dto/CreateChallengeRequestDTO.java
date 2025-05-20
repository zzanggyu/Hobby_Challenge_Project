package com.hobby.challenge.fobackend.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class CreateChallengeRequestDTO {
    @NotBlank(message = "제목은 반드시 입력해야 합니다.")
    private String title;
    
    @NotNull(message = "챌린지에 대한 설명을 적어주세요")
    @Size(min = 10, message = "10자이상 입력하세요")
    private String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @NotNull(message = "시작일을 선택하세요.")
    private LocalDate startDate;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @NotNull(message = "종료일을 선택하세요.")
    @Future(message = "종료일은 시작일로부터 일주일 이후여야 합니다.")
    private LocalDate endDate;

    @NotNull(message = "카테고리를 선택하세요.")
    private Integer categoryId;
}
