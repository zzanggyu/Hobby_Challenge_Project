package com.hobby.challenge.fobackend.dto;import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CertCommentRequestDTO {
	@Size(max = 100, message = "댓글은 100글자까지 입력 가능합니다.")
	private String content; // 댓글 
}
