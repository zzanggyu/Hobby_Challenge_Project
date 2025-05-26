package com.hobby.challenge.fobackend.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.hobby.challenge.fobackend.dto.CertificationDTO;
import com.hobby.challenge.fobackend.dto.ParticipationResponseDTO;
import com.hobby.challenge.fobackend.entity.Certification;
import com.hobby.challenge.fobackend.exception.CustomException;
import com.hobby.challenge.fobackend.exception.ErrorCode;
import com.hobby.challenge.fobackend.mapper.CertificationMapper;
import com.hobby.challenge.fobackend.mapper.ParticipationMapper;
import com.hobby.challenge.fobackend.service.CertificationService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CertificationServiceImpl implements CertificationService {

	private final CertificationMapper certificationMapper;
	private final ParticipationMapper participationMapper;
	// TODO: 실제 이미지 파일 저장을 위한 서비스 (S3) 사용
	
	// 인증내역 가져오기
	@Override
	public List<CertificationDTO> getCertifications(Integer userId, Integer challengeId) {
		// 승인된 참여자인지 확인
	    if (participationMapper.selectByUserAndChallenge(userId, challengeId) == null) {
	    	throw new CustomException(ErrorCode.CERTIFICATION_ACCESS_DENIED, "인증은 승인된 참여자만 가능합니다.");
	    }
		return certificationMapper.findByChallenge(challengeId);
	}

	// 인증 등록하기
	@Override
	@Transactional
	public CertificationDTO submitCertification(
			Integer userId,
			Integer challengeId, 
			MultipartFile file, 
			String comment
			
	) {
	    ParticipationResponseDTO participation =
	            participationMapper.selectByUserAndChallenge(userId, challengeId);
	        if (participation == null || !"APPROVED".equals(participation.getStatus())) {
	            throw new CustomException(
	                ErrorCode.PARTICIPATION_NOT_APPROVED,
	                "인증을 등록하려면 먼저 챌린지 참여 승인을 받아야 합니다."
	            );
	        }
		
        //  챌린지-사용자 참여 ID 조회
        Integer participationId = participationMapper
            .selectByUserAndChallenge(userId, challengeId)
            .getParticipationId();
		
        // 파일 저장 → URL 생성 (예: S3 업로드, 로컬 저장)
        // String fileUrl = storage.store(file);
        String imageUrl = "/uploads/" + file.getOriginalFilename();

        // 엔티티 생성 & DB에 저장
        Certification cert = Certification.builder()
        		.participationId(participationId)
                .imageUrl(imageUrl)           // 엔티티 필드에 맞춰서
                .comment(comment)
                .likeCount(0)
                .createdBy(userId)            // 엔티티 필드명(createdBy)
                .createdDate(LocalDateTime.now())
                .build();
        
        // 하루에 한 건의 인증만 허용
        try {
            certificationMapper.insertCertification(cert);
        } catch (DuplicateKeyException e) {
            throw new CustomException(
                    ErrorCode.DUPLICATE_CERTIFICATION,
                    "오늘은 이미 인증을 등록했습니다.");
        }
//        certificationMapper.insertCertification(cert);

        // DTO로 변환해서 반환
        return certificationMapper.findByChallenge(challengeId).stream()
                .filter(d -> d.getCertificationId().equals(cert.getCertificationId()))
                .findFirst()
                .orElseThrow();
	}

}
