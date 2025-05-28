package com.hobby.challenge.fobackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParticipantDTO {
	private Integer participationId;
    private Integer userId;
    private String nickname;
    private String imageUrl;  // 프로필 이미지 URL
    private String role;
}
