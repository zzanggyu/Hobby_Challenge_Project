package com.hobby.challenge.fobackend.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.hobby.challenge.fobackend.dto.CertificationDTO;

public interface CertificationService {
	// 주어진 챌린지의 모든 인증 내역 조회
    List<CertificationDTO> getCertifications(Integer userId, Integer challengeId);
    
    // 챌린지에 인증 등록
    CertificationDTO submitCertification(
    		Integer userId,
            Integer challengeId,
            MultipartFile file, // 이미지
            String comment
            
        );
}
