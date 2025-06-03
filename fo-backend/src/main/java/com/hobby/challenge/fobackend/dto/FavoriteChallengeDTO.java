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
public class FavoriteChallengeDTO {
    private Integer userId;          // 복합키 1
    private Integer challengeId;     // 복합키 2
    private LocalDateTime createdDate;   // FC.CREATED_DATE
	private ChallengeResponseDTO challenge;
	
	
    // 참여 상태 표시용 필드 추가
    private Boolean participating;  // 참여중 여부 (APPROVED)
    private Boolean requesting;     // 요청중 여부 (REQUESTED)
}
