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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.hobby.challenge.fobackend.dto.CertificationDTO;
import com.hobby.challenge.fobackend.service.CertificationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/challenges/{challengeId}/certifications")
@RequiredArgsConstructor
public class CertificationController {
	private final CertificationService certificationService;

    // 인증 내역 조회 
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


    // 인증 등록 (FormData: file, comment) 
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CertificationDTO> submitCertification(
        @PathVariable("challengeId") Integer challengeId,
        @RequestParam("file") MultipartFile file,
        @RequestParam("comment") String comment,
        @AuthenticationPrincipal(expression="userId") Integer userId
    ) {
        CertificationDTO created = certificationService
            .submitCertification( userId,challengeId, file, comment);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    
    // 인증 수정
    @PutMapping("/{certId}")
    public ResponseEntity<CertificationDTO> update(
        @RequestHeader("X-USER-ID") Integer userId,
        @PathVariable Integer challengeId,
        @PathVariable("certId") Integer certId,
        @RequestPart(required=false) MultipartFile file,
        @RequestPart String comment
    ) {
        return ResponseEntity.ok(
            certificationService.updateCertification(userId, challengeId, certId, file, comment)
        );
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

}
