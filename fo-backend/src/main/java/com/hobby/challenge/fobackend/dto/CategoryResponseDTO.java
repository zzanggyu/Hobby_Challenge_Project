package com.hobby.challenge.fobackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class CategoryResponseDTO {
	private Integer id; // 카테고리 아이디
	private String name; // 카테고리 취미명
	
}
