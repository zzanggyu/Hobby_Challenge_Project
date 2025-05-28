package com.hobby.challenge.fobackend.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
@Mapper
public interface CertLikeMapper {
	
	// 좋아요 토글 관련 매퍼
	// 좋아요 여부
    int isLiked(@Param("certificationId") int certificationId, @Param("userId") int userId);
	
    // 좋아요 삽입
    void insertLike(@Param("certificationId") int certificationId, @Param("userId") int userId);

	// 좋아요 삭제
    void deleteLike(@Param("certificationId") int certificationId, @Param("userId") int userId);
	
    // 좋아요 수 +1
	void incrementLikeCount(int certificationId);
	
	// 좋아요 수 -1
	void decrementLikeCount(int certificationId);
}
