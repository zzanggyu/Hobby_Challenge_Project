package com.hobby.challenge.fobackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateParticipationDTO {
	private Integer participationId;
	private Integer userId; // 참여 요청자 아이디
	private Integer challengeId;
	private String nickname;
	private String  status; 
	private String  role;  
}
