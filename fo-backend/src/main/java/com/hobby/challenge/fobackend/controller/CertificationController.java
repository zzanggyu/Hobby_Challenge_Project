package com.hobby.challenge.fobackend.controller;



import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hobby.challenge.fobackend.dto.CertificationDTO;
import com.hobby.challenge.fobackend.dto.CertificationRequestDTO;
import com.hobby.challenge.fobackend.service.CertificationService;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@RestController
@RequestMapping("/api/challenges/{challengeId}/certifications")
@RequiredArgsConstructor
public class CertificationController {
	private final CertificationService certificationService;
//	private final S3StorageService s3StorageService;
    private final S3Presigner presigner;     // S3Config에서 빈으로 등록한 Presigner
    private final String s3BucketName;       // S3Config에서 빈으로 등록한 버킷 이름
    
    
	// 인증 등록 (FormData: file, comment) 
    @PostMapping
    public ResponseEntity<CertificationDTO> submitCertification(
        @PathVariable("challengeId") Integer challengeId,
        @RequestBody CertificationRequestDTO req,  // <<== JSON 바디로 받기!
        @AuthenticationPrincipal(expression="userId") Integer userId
    ) {
        CertificationDTO dto =
            certificationService.submitCertification(
                userId, challengeId, req.getImageKey(), req.getComment()
            );
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }
    
    // 인증 좋아요 토글
    @PostMapping("/{certificationId}/like")
    public ResponseEntity<?> toggleLike(
        @PathVariable("certificationId") int certificationId,
        @AuthenticationPrincipal(expression = "userId") Integer userId) {

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

	

    // 프리사인드 URL 발급
    @GetMapping("/presign")
    public ResponseEntity<Map<String, String>> presignUpload(
            @PathVariable("challengeId") int challengeId,
            @RequestParam("filename") String filename, // 사용자가 업로드할 원본 파일명
            @RequestParam("contentType") String contentType // ex: "image/png"
    ) {
    	
    	 System.out.println("#### presign contentType: " + contentType);
        // 키 생성 (중복 방지 UUID 포함)
        String key = "certifications/" + challengeId + "/" +
                     UUID.randomUUID() + "_" + filename;

        // S3 PUT 요청 정보
        PutObjectRequest objectRequest = PutObjectRequest.builder()
            .bucket(s3BucketName)
            .key(key)
            .contentType(contentType)
            .acl(ObjectCannedACL.PUBLIC_READ)
            .build();

        // 15분 동안 유효한 presigned URL
        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(15))
                .putObjectRequest(objectRequest)
                .build();

        PresignedPutObjectRequest presigned = presigner.presignPutObject(presignRequest);
        
      
        String presignedUrl = presigned.url().toString();
        System.out.println("✅ PRESIGNED URL: " + presignedUrl);
        
        return ResponseEntity.ok(Map.of(
            "url", presigned.url().toString(),
            "key", key
        ));
    }

    


    // 인증 목록 조회 
    @GetMapping
    public ResponseEntity<List<CertificationDTO>> getCertifications(
        @PathVariable("challengeId") Integer challengeId,
        @AuthenticationPrincipal(expression="userId") Integer userId
    ) {
        List<CertificationDTO> list = certificationService.getCertifications(userId, challengeId);
        return ResponseEntity.ok(list);
    }
    
    // 인증 단건 조회
    @GetMapping("/{certificationId}")
    public ResponseEntity<CertificationDTO> getCertificationDetail(
        @PathVariable("challengeId") Integer challengeId,
        @PathVariable("certificationId") Integer certificationId,
        @AuthenticationPrincipal(expression = "userId") Integer userId
    ) {
        CertificationDTO cert = certificationService.getCertificationDetail(userId, certificationId);
        return ResponseEntity.ok(cert);
    }



    
    // 인증 수정 (프리사인드 이미지키 + 코멘트) 
    @PutMapping("/{certId}")
    public ResponseEntity<CertificationDTO> updateCertification(
        @AuthenticationPrincipal(expression="userId") Integer userId, // JWT에서 추출
        @PathVariable("challengeId") Integer challengeId,
        @PathVariable("certificationId") Integer certificationId,
        @RequestBody CertificationRequestDTO req                             // 수정 코멘트
    ) {
        CertificationDTO updated = certificationService.updateCertification(
            userId,
            challengeId,
            certificationId,
            req.getImageKey(),
            req.getComment()
        );
        return ResponseEntity.ok(updated);
    }
    
    // 인증 삭제
    @DeleteMapping("/{certificationId}")
    public ResponseEntity<Void> deleteCertification(
        @PathVariable("challengeId") Integer challengeId,
        @PathVariable("certificationId") Integer certificationId,
        @AuthenticationPrincipal(expression="userId") Integer userId
    ) {
        certificationService.deleteCertification(userId, challengeId, certificationId);
        return ResponseEntity.noContent().build();
    }
    
    
    


}
