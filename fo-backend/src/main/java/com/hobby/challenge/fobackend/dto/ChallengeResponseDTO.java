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
    private Integer createdBy; 
    
    private String challengeStatus; // 챌린지 상태
    
    private Boolean isFavorite; // 관심 챌린지 하트 여부
    private Integer favoriteCount; // 좋아요(관심 등록) 수
    
    // USER NICKNAME
    private String creatorNickname;

}
