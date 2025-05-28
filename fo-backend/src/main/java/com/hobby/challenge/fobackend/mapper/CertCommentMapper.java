package com.hobby.challenge.fobackend.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.hobby.challenge.fobackend.dto.CertCommentResponseDTO;
import com.hobby.challenge.fobackend.entity.CertComment;

@Mapper
public interface CertCommentMapper {
    // 댓글 등록
  	void insertComment (CertComment certComment);
  	
  	// 댓글 목록 조회
  	List<CertCommentResponseDTO> findByCertId(@Param("certificationId") int certificationId);
  	
  	// 댓글 수정
  	int updateComment(@Param("commentId") int commentId,
  			@Param("createdBy") int createdBy,
  			@Param("content") String content);
  	
  	// 댓글 삭제
  	int deleteComment(@Param("commentId") int commentId,
  			@Param("createdBy") int createdBy);
  	
}
