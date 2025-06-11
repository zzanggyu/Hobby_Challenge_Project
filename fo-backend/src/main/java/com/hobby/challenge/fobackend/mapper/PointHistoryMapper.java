package com.hobby.challenge.fobackend.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.hobby.challenge.fobackend.entity.PointHistory;

import java.util.List;

@Mapper
public interface PointHistoryMapper {
    
    // 포인트 히스토리 등록
    void insert(PointHistory pointHistory);
    
    // 사용자별 포인트 히스토리 조회
    List<PointHistory> findByUserId(@Param("userId") Integer userId);
    
    // 사용자 총 포인트 계산
    Integer getTotalPointsByUserId(@Param("userId") Integer userId);
}