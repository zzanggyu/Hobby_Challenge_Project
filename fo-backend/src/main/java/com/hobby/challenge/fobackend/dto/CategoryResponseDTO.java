package com.hobby.challenge.fobackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryResponseDTO {
	private Integer categoryId; // 카테고리 아이디
	private String categoryName; // 카테고리 취미명
	
}
