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
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateChallengeRequestDTO {

	// 제목
    @NotBlank(message = "제목은 반드시 입력해야 합니다.")
    @Size(min = 2, max = 50, message = "제목을 2~50자로 입력하세요.")
    private String title;

    // 설명 
    @NotNull(message = "설명을 입력하세요.")
    @Size(min = 10, max = 500, message = "설명을 10~500자로 입력하세요.")
    private String description;

    //일정
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @NotNull(message = "시작일을 선택하세요.")
    private LocalDate startDate;    // 값이 없으면 변경하지 않음

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @NotNull(message = "종료일을 선택하세요.")
    @Future(message = "종료일은 미래여야 합니다.")
    private LocalDate endDate;

    // 카테고리
    @NotNull(message = "카테고리를 선택하세요.")
    private Integer categoryId;     
}
