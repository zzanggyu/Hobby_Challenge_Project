package com.hobby.challenge.fobackend.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.hobby.challenge.fobackend.entity.Category;

@Mapper
public interface CategoryMapper {

	// 전체 카테고리 챌린지 생성 시 목록 드롭다운할 때 사용
	List<Category> findAll();
	
	// 단일 카테고리 챌린지 조회시 카테고리별로 검색할 때 사용
	Category findByCategory(@Param("categoryId") Integer categoryId);
 
}
