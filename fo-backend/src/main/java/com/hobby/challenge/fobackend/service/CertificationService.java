package com.hobby.challenge.fobackend.service;

import org.springframework.web.multipart.MultipartFile;

import com.hobby.challenge.fobackend.dto.CertificationDTO;
import com.hobby.challenge.fobackend.dto.PageResponseDTO;

public interface CertificationService {
	// 주어진 챌린지의 모든 인증 내역 조회 페이징
    PageResponseDTO<CertificationDTO> getCertifications(
            Integer userId, 
            Integer challengeId, 
            int size, 
            int offset, 
            boolean onlyMine
        );
        
    // 인증 상세 조회
    CertificationDTO getCertificationDetail(Integer userId, Integer certificationId);
    
    // 챌린지에 인증 등록
	CertificationDTO submitCertification(
	    Integer userId,
	    Integer challengeId,
	    MultipartFile image,
	    String comment
 );
    
    // 좋아요 토글
    boolean toggleLike(int certificationId, int userId);
    
    // 인증 수정
    CertificationDTO updateCertification(
            Integer userId, 
            Integer challengeId,
            Integer certificationId,
            MultipartFile image,
            String comment);
    
    // 인증 삭제
    void deleteCertification(Integer userId, Integer challengeId, Integer certificationId);
    
    
}
