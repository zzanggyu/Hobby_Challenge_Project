	package com.hobby.challenge.fobackend.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ChallengeResponseDTO {
	private Integer challengeId; 
    private String title;
    private String description;
    private Integer categoryId;
    private String creatorNickname;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDateTime createdDate;

}
