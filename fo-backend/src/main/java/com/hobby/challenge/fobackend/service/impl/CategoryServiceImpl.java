package com.hobby.challenge.fobackend.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hobby.challenge.fobackend.dto.CategoryResponseDTO;
import com.hobby.challenge.fobackend.entity.Category;
import com.hobby.challenge.fobackend.exception.CustomException;
import com.hobby.challenge.fobackend.exception.ErrorCode;
import com.hobby.challenge.fobackend.mapper.CategoryMapper;
import com.hobby.challenge.fobackend.service.CategoryService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService{
	private final CategoryMapper categoryMapper;
	
	// 전체 카테고리 조회
	@Override
	@Transactional(readOnly = true)
	public List<CategoryResponseDTO> getAllCategories(){
		List<Category> categories = categoryMapper.findAll();
		
		return categories.stream()
				.map(c -> new CategoryResponseDTO(c.getCategoryId(), c.getCategoryName()))
				.collect(Collectors.toList());
	}
	
    // 단일 카테고리 조회 
    @Override
    @Transactional(readOnly = true)
    public CategoryResponseDTO getCategoryById(Integer categoryId) {
        Category c = categoryMapper.findByCategory(categoryId);
        if (c == null) {
            
            throw new CustomException(ErrorCode.CATEGORY_NOT_FOUND, "카테고리 ID=" + categoryId);
        }
        return new CategoryResponseDTO(c.getCategoryId(), c.getCategoryName());
    }
}
