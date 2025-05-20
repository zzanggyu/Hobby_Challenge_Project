package com.hobby.challenge.fobackend.service;

import java.util.List;

import com.hobby.challenge.fobackend.dto.CategoryResponseDTO;

public interface CategoryService {
	// 전체 카테고리 조회 
    List<CategoryResponseDTO> getAllCategories();
    
    // 단일 카테고리 조회
    CategoryResponseDTO getCategoryById(Integer categoryId);
}
