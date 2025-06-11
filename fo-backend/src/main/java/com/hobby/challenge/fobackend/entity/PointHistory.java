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
public class PointHistory {
    private Integer historyId;
    private Integer userId;
    private String type;  // TYPE ('CERT_UPLOAD','CHALLENGE_SUCCESS','ADMIN_GRANT','PENALTY')
    private Integer points;
    private LocalDateTime createdDate;
}