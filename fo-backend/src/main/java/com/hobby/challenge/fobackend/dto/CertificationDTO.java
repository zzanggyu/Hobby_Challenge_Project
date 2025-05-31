package com.hobby.challenge.fobackend.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

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
	  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	  private LocalDate certDate; // 인증 날짜 (시간 제외)
	  private Integer likeCount; // 좋아요 수

	  private Integer level; // 인증 목록에 보여 주기 위함
}
