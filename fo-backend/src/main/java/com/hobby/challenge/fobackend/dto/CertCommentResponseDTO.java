package com.hobby.challenge.fobackend.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CertCommentResponseDTO {
    private Integer commentId;
    private Integer certId;
    private Integer createdBy;
    private String nickname; // 작성자 닉네임
    private String content; // 댓글 내용
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
}	
