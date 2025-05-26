package com.hobby.challenge.fobackend.entity;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Certification {
    private Integer certificationId;
    private Integer participationId; 
    private String imageUrl;
    private String comment;
    private Integer likeCount;
    private Integer createdBy;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
    
    
}