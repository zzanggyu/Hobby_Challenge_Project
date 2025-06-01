package com.hobby.challenge.fobackend.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.hobby.challenge.fobackend.dto.CertCommentResponseDTO;
import com.hobby.challenge.fobackend.dto.CertificationDTO;
import com.hobby.challenge.fobackend.entity.CertComment;
import com.hobby.challenge.fobackend.exception.CustomException;
import com.hobby.challenge.fobackend.exception.ErrorCode;
import com.hobby.challenge.fobackend.mapper.CertCommentMapper;
import com.hobby.challenge.fobackend.mapper.CertificationMapper;
import com.hobby.challenge.fobackend.service.CertCommentService;
import com.hobby.challenge.fobackend.service.NotificationService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CertCommentServiceImpl implements CertCommentService {
	private final CertCommentMapper certCommentMapper;
    private final CertificationMapper certificationMapper; 
    private final NotificationService notificationService; 
	
	// 댓글 등록, 인증 작성자에게 알림
	@Override
	public void addComment(int certificationId, int userId, String content) {
		CertComment comment = new CertComment();
		comment.setCertificationId(certificationId);
		comment.setCreatedBy(userId);
		comment.setContent(content);
		certCommentMapper.insertComment(comment);
		
        // 알림 생성: 인증 작성자에게 새 댓글 알림 (본인 댓글 제외)
        CertificationDTO cert = certificationMapper.selectById(certificationId);
        if (!cert.getUserId().equals(userId)) {
            notificationService.createNewCommentNotification(
                cert.getUserId(), 
                certificationId
            );
        }
		
	}
	
	// 댓글 목록 조회
	@Override
	public List<CertCommentResponseDTO> getComments(int certificationId) {
		return certCommentMapper.findByCertId(certificationId);
	}
	
	// 댓글 수정
	@Override
	public void updateComment(int commentId, int userId, String content) {
		int updated = certCommentMapper.updateComment(commentId, userId, content);
	    if (updated == 0) {
	        throw new CustomException(ErrorCode.COMMENT_UPDATE_FORBIDDEN, "본인의 댓글만 수정할 수 있습니다.");
	    }

	}
	
	// 댓글 삭제
	@Override
	public void deleteComment(int commentId, int userId) {
		int deleted = certCommentMapper.deleteComment(commentId, userId);
		
		if(deleted == 0) {
			throw new CustomException(ErrorCode.COMMENT_DELETE_FORBIDDEN, "댓글 삭제 권한이 없거나 존재하지 않는 댓글입니다.");
		}

	}

}
