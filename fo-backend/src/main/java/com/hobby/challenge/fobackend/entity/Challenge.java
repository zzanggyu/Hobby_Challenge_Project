package com.hobby.challenge.fobackend.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Challenge {
    private Integer challengeId;
    private String title;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer categoryId;
    private Integer createdBy; // 외래키 userId 참조한다.
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
    
    private Boolean isFavorite; // DB에는 없지만 관심 챌린지 여부를 알기 위해 가상 컬럼 추가
    
    // 조인 결과
    private User creator; 
}
