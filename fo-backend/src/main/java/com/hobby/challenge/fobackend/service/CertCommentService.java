package com.hobby.challenge.fobackend.service;

import java.util.List;

import com.hobby.challenge.fobackend.dto.CertCommentResponseDTO;

public interface CertCommentService {
	
    // 댓글 등록
    void addComment(int certId, int userId, String content);
	
    // 댓글 목록 조회
    List<CertCommentResponseDTO> getComments(int certificationId);

    // 댓글 수정
    void updateComment(int commentId, int userId, String content);

    // 댓글 삭제
    void deleteComment(int commentId, int userId);

}
