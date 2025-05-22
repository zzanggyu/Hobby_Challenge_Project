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
public class FavoriteChallenge {
	private Integer userId;
	private Integer challengeId;
	private LocalDateTime createdDate;
}
