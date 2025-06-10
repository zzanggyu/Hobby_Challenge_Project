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
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateChallengeRequestDTO {
    @NotBlank(message = "제목은 반드시 입력해야 합니다.")
    @Size(min = 2, max = 50, message = "제목을 2글자이상 50글자 이내로 입력하세요")
    private String title;
    
    @NotNull(message = "챌린지에 대한 설명을 적어주세요")
    @Size(min = 10, max = 500, message = "설명을 10글자이상 500글자 이하로 입력하세요")
    private String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @NotNull(message = "시작일을 선택하세요.")
    private LocalDate startDate;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @NotNull(message = "종료일을 선택하세요.")
    private LocalDate endDate;

    @NotNull(message = "카테고리를 선택하세요.")
    private Integer categoryId;
}
