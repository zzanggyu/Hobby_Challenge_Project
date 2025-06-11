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
    
    // 오늘 특정 타입의 포인트를 받았는지 체크
    boolean hasReceivedPointsToday(@Param("userId") Integer userId, 
                                   @Param("type") String type);
    
    // 사용자 총 포인트 계산
    Integer getTotalPointsByUserId(@Param("userId") Integer userId);
}