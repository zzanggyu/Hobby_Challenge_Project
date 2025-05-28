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
public class CertificationDTO {
	  private Integer certificationId;
	  private Integer userId; // 인증 올린 사람
	  private String nickname; // 인증 올린 사람 닉네임
	  private String comment;
	  private String imageUrl; // 이미지 경로
	  private LocalDateTime createdDate;
	  
	  private Integer likeCount; // 좋아요 수

	  private Integer level; // 인증 목록에 보여 주기 위함
}
