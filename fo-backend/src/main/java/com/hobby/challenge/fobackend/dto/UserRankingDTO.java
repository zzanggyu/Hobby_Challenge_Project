package com.hobby.challenge.fobackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRankingDTO {
    private Integer userId;
    private String nickname;
    private String imageUrl;           // 프로필 이미지
    private Integer level;             // 사용자 레벨
    private Integer certificationCount; // 총 인증 수
    private Integer points;
}