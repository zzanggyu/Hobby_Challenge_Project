package com.hobby.challenge.fobackend.dto;



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
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime createdDate;
    
    // USER NICKNAME
    private String creatorNickname;

}
