package com.hobby.challenge.fobackend.controller;




import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.hobby.challenge.fobackend.dto.CertificationDTO;
import com.hobby.challenge.fobackend.dto.PageResponseDTO;
import com.hobby.challenge.fobackend.exception.CustomException;
import com.hobby.challenge.fobackend.exception.ErrorCode;
import com.hobby.challenge.fobackend.service.CertificationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/challenges/{challengeId}/certifications")
@RequiredArgsConstructor
public class CertificationController {
	private final CertificationService certificationService;
//	private final S3StorageService s3StorageService;
//    private final S3Presigner presigner;     // S3Config에서 빈으로 등록한 Presigner
//    private final String s3BucketName;       // S3Config에서 빈으로 등록한 버킷 이름
    
    
	// 인증 등록 (FormData: file, comment) 
    // 인증 등록 - MultipartFile 직접 받기
    @PostMapping
    public ResponseEntity<CertificationDTO> submitCertification(
        @PathVariable("challengeId") Integer challengeId,
        @RequestParam("comment") String comment,
        @RequestPart(value = "image", required = false) MultipartFile image,
        @AuthenticationPrincipal(expression="userId", errorOnInvalidType = false) Integer userId) {
        // 이미지 필수 체크
        if (image == null || image.isEmpty()) {
            throw new CustomException(ErrorCode.FILE_REQUIRED, "인증 사진은 필수입니다.");
        }
        
        CertificationDTO dto = certificationService.submitCertification(
            userId, challengeId, image, comment
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }
    // 인증 좋아요 토글
    @PostMapping("/{certificationId}/like")
    public ResponseEntity<?> toggleLike(
        @PathVariable("certificationId") int certificationId,
        @AuthenticationPrincipal(expression="userId", errorOnInvalidType = false) Integer userId) {

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        try {
            boolean isLiked = certificationService.toggleLike(certificationId, userId);
            return ResponseEntity.ok().body(Map.of("liked", isLiked));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("존재하지 않는 인증입니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("좋아요 처리 중 오류 발생");
        }
    }

	

    



    // 인증 목록 조회 - 페이징 추가
    @GetMapping
    public ResponseEntity<PageResponseDTO<CertificationDTO>> getCertifications(
        @PathVariable("challengeId") Integer challengeId,
        @RequestParam(name = "page", defaultValue = "1") int page,
        @RequestParam(name = "size", defaultValue = "20") int size,
        @RequestParam(name = "onlyMine", defaultValue = "false") boolean onlyMine,
        @AuthenticationPrincipal(expression="userId", errorOnInvalidType = false) Integer userId
    ) {
        // 페이징 처리: offset 계산
        int offset = (page - 1) * size;
        
        // 서비스 호출 (페이징 파라미터 추가)
        PageResponseDTO<CertificationDTO> result = certificationService.getCertifications(
            userId, challengeId, size, offset, onlyMine
        );
        
        return ResponseEntity.ok(result);
    }
    
    // 인증 단건 조회
    @GetMapping("/{certificationId}")
    public ResponseEntity<CertificationDTO> getCertificationDetail(
        @PathVariable("challengeId") Integer challengeId,
        @PathVariable("certificationId") Integer certificationId,
        @AuthenticationPrincipal(expression="userId", errorOnInvalidType = false) Integer userId) {
        CertificationDTO cert = certificationService.getCertificationDetail(userId, certificationId);
        return ResponseEntity.ok(cert);
    }



    
    // 인증 수정 (프리사인드 이미지키 + 코멘트) 
    // 인증 수정도 MultipartFile로 변경
    @PutMapping(value = "/{certificationId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CertificationDTO> updateCertification(
    	@AuthenticationPrincipal(expression="userId", errorOnInvalidType = false) Integer userId,
        @PathVariable("challengeId") Integer challengeId,
        @PathVariable("certificationId") Integer certificationId,
        @RequestParam("comment") String comment,
        @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        CertificationDTO updated = certificationService.updateCertification(
            userId, challengeId, certificationId, image, comment
        );
        return ResponseEntity.ok(updated);
    }
    
    // 인증 삭제
    @DeleteMapping("/{certificationId}")
    public ResponseEntity<Void> deleteCertification(
        @PathVariable("challengeId") Integer challengeId,
        @PathVariable("certificationId") Integer certificationId,
        @AuthenticationPrincipal(expression="userId", errorOnInvalidType = false) Integer userId) {
        certificationService.deleteCertification(userId, challengeId, certificationId);
        return ResponseEntity.noContent().build();
    }
    
    
    


}
