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
public class CertComment {
    private Integer commentId;
    private Integer certificationId;
    private Integer createdBy;
    private String content;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
}
