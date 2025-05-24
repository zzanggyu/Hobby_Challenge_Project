package com.hobby.challenge.fobackend.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParticipationResponseDTO {
    private Integer participationId;
    private Integer userId;
    private Integer challengeId;
    private String status; // 참여 상태
    private LocalDateTime requestDate;
    private LocalDateTime participatedDate;
    private String role;
}
