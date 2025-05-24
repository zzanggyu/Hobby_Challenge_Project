package com.hobby.challenge.fobackend.dto;



import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChallengeResponseDTO {
	private Integer challengeId; 
    private String title;
    private String description;
    private Integer categoryId;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDateTime createdDate;
    
    private Boolean isFavorite; // 관심 챌린지 하트 여부
    
    // USER NICKNAME
    private String creatorNickname;

}
