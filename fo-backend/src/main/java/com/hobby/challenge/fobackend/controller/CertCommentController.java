package com.hobby.challenge.fobackend.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hobby.challenge.fobackend.dto.CertCommentRequestDTO;
import com.hobby.challenge.fobackend.dto.CertCommentResponseDTO;
import com.hobby.challenge.fobackend.service.CertCommentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/certifications/{certificationId}/comments")
public class CertCommentController {
	private final CertCommentService certCommentService;
	
	// 댓글 등록
	@PostMapping
	public void addComment(@PathVariable("certificationId") int certificationId,
							@Valid @RequestBody CertCommentRequestDTO dto,
							@AuthenticationPrincipal(expression = "userId", errorOnInvalidType = false) int userId){
		certCommentService.addComment(certificationId, userId, dto.getContent());
	}
	
	// 댓글 목록 조회
    @GetMapping
    public List<CertCommentResponseDTO> getComments(@PathVariable("certificationId") int certificationId) {
        return certCommentService.getComments(certificationId);
    }
	

    // 댓글 수정
    @PutMapping("/{commentId}")
    public void updateComment(@PathVariable("certificationId") int certificationId,
                              @PathVariable("commentId") int commentId,
                              @Valid @RequestBody CertCommentRequestDTO dto,
                              @AuthenticationPrincipal(expression = "userId", errorOnInvalidType = false) int userId) {
        certCommentService.updateComment(commentId, userId, dto.getContent());
    }
	
    // 댓글 삭제
    @DeleteMapping("/{commentId}")
    public void deleteComment(@PathVariable("certificationId") int certificationId,
                              @PathVariable("commentId") int commentId,
                              @AuthenticationPrincipal(expression = "userId", errorOnInvalidType = false) int userId) {
        certCommentService.deleteComment(commentId, userId);
    }
}
