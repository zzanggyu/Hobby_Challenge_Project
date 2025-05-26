package com.hobby.challenge.fobackend.controller;


import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;

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
}
