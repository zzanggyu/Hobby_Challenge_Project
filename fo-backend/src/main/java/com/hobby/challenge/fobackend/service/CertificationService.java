package com.hobby.challenge.fobackend.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.hobby.challenge.fobackend.dto.CertificationDTO;

public interface CertificationService {
	// 주어진 챌린지의 모든 인증 내역 조회
    List<CertificationDTO> getCertifications(Integer userId, Integer challengeId);
    
    // 인증 상세 조회
    CertificationDTO getCertificationDetail(Integer userId, Integer certificationId);
    
    // 챌린지에 인증 등록
	CertificationDTO submitCertification(
	    Integer userId,
	    Integer challengeId,
	    String imageKey,
	    String comment
 );
    
    // 좋아요 토글
    boolean toggleLike(int certificationId, int userId);
    
    // 인증 수정
    CertificationDTO updateCertification(
            Integer userId, 
            Integer challengeId,
            Integer certificationId,
            String imageKey, 
            String comment);
    
    // 인증 삭제
    void deleteCertification(Integer userId, Integer challengeId, Integer certificationId);
}
