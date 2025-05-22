package com.hobby.challenge.fobackend.entity;

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
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer categoryId;
    private Integer createdBy; // 외래키 userId 참조한다.
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
    
    // 조인 결과
    private User creator; 
}
