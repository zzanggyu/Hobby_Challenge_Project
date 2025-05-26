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
public class ChallengeDetailDTO {
    private Integer challengeId;
    private String title;
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer categoryId;
    private Integer createdBy;
    private Boolean isFavorite;  // 즐겨찾기 여부
    private Boolean joined;      // 참여 여부
    private String creatorNickname; // 생성자 정보 (userId, nickname)
}
