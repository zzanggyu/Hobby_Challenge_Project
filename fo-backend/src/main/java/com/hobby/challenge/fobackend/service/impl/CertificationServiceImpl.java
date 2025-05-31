package com.hobby.challenge.fobackend.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hobby.challenge.fobackend.dto.CertificationDTO;
import com.hobby.challenge.fobackend.dto.ParticipationResponseDTO;
import com.hobby.challenge.fobackend.entity.Certification;
import com.hobby.challenge.fobackend.entity.Challenge;
import com.hobby.challenge.fobackend.exception.CustomException;
import com.hobby.challenge.fobackend.exception.ErrorCode;
import com.hobby.challenge.fobackend.mapper.CertLikeMapper;
import com.hobby.challenge.fobackend.mapper.CertificationMapper;
import com.hobby.challenge.fobackend.mapper.ChallengeMapper;
import com.hobby.challenge.fobackend.mapper.ParticipationMapper;
import com.hobby.challenge.fobackend.service.CertificationService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CertificationServiceImpl implements CertificationService {

	private final CertificationMapper certificationMapper;
	private final ParticipationMapper participationMapper;
	private final ChallengeMapper challengeMapper;
	private final CertLikeMapper certLikeMapper;
	// TODO: 실제 이미지 파일 저장을 위한 서비스 (S3) 사용
	@Value("${aws.s3.bucket-name}")
	private String bucketName;
	@Value("${aws.s3.region}")
	private String region;
	
//	private final S3StorageService s3StorageService;
	// 허용할 확장자/타입
//	private static final List<String> ALLOWED_TYPES = List.of("image/jpeg", "image/png", "image/gif");
//	private static final long MAX_SIZE = 5 * 1024 * 1024; // 5MB 파일 크기 제한
	
	// 인증 등록하기
	@Override
	@Transactional
	public CertificationDTO submitCertification(
			Integer userId,
			Integer challengeId, 
			String imageKey,
			String comment) {
	
		// 1) imageKey 유효성 검증
		if (imageKey == null || imageKey.isBlank()) {
		  throw new CustomException(
		    ErrorCode.FILE_REQUIRED,
		    "이미지 키가 전달되지 않았습니다."
		  );
		}

	    ParticipationResponseDTO participation =
	            participationMapper.selectByUserAndChallenge(userId, challengeId);
	    	// 참여 승인 확인
	        if (participation == null || !"APPROVED".equals(participation.getStatus())) {
	            throw new CustomException(
	                ErrorCode.PARTICIPATION_NOT_APPROVED,
	                "인증을 등록하려면 먼저 챌린지 참여 승인을 받아야 합니다."
	            );
	        }
	        
       // 챌린지 기간 검증
       Challenge c = challengeMapper.selectById(challengeId, null);
       LocalDate today = LocalDate.now();
       if ( today.isBefore(c.getStartDate()) || today.isAfter(c.getEndDate()) ) {
           throw new CustomException(
               ErrorCode.CERTIFICATION_INVALID_PERIOD,
               "챌린지 기간 내에만 인증할 수 있습니다."
           );
       }

        //  챌린지-사용자 참여 ID 조회
       Integer participationId = participationMapper
            .selectByUserAndChallenge(userId, challengeId)
            .getParticipationId();
		

       
       // imageKey → S3 퍼블릭 URL 조합
       String url = String.format(
    		     "https://%s.s3.%s.amazonaws.com/%s",
    		     bucketName,
    		     region,
    		     imageKey
    		   );

        // 엔티티 생성 & DB에 저장
        Certification cert = Certification.builder()
        		.participationId(participationId)
        		.imageUrl(url)          // 엔티티 필드에 맞춰서
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

        // DTO로 변환해서 반환
        return certificationMapper
        		.findByChallenge(challengeId)
        		.stream()
                .filter(d -> d.getCertificationId().equals(cert.getCertificationId()))
                .findFirst()
                .orElseThrow();
	}
	
	// 인증내역 가져오기
	@Override
	public List<CertificationDTO> getCertifications(Integer userId, Integer challengeId) {
		// 승인된 참여자인지 확인
	    if (participationMapper.selectByUserAndChallenge(userId, challengeId) == null) {
	    	throw new CustomException(ErrorCode.CERTIFICATION_ACCESS_DENIED, "인증은 승인된 참여자만 가능합니다.");
	    }
		return certificationMapper.findByChallenge(challengeId);
	}
	
	// 인증 상세 가져오기
    @Override
    public CertificationDTO getCertificationDetail(Integer userId, Integer certificationId) {
        CertificationDTO dto = certificationMapper.selectById(certificationId);
        if (dto==null) throw new CustomException(ErrorCode.NOT_FOUND_CERTIFICATION, "인증을 찾을 수 없습니다.");
        // 접근 권한 추가 검증 가능
        return dto;
    }


	
	// 좋아요 토글
	@Override
	public boolean toggleLike(int certificationId, int userId) {
	    CertificationDTO cert = certificationMapper.selectById(certificationId);
	    if (cert == null) {
	        throw new NoSuchElementException("해당 인증이 존재하지 않습니다.");
	    }



	    boolean alreadyLiked = certLikeMapper.isLiked(certificationId, userId) > 0;

	    if (alreadyLiked) {
	    	certLikeMapper.deleteLike(certificationId, userId);
	    	certLikeMapper.decrementLikeCount(certificationId);
	        return false;
	    } else {
	    	certLikeMapper.insertLike(certificationId, userId);
	    	certLikeMapper.incrementLikeCount(certificationId);
	        return true;
	    }
	}

	
	// 인증 수정
    @Override
    @Transactional
    public CertificationDTO updateCertification(
        Integer userId,
        Integer challengeId,
        Integer certificationId,
        String imageKey,
        String comment
    ) {
        // 1) 기존 인증 조회 및 본인 여부 검증
        CertificationDTO old = getCertificationDetail(userId, certificationId);
        if (!old.getUserId().equals(userId)) {
            throw new CustomException(
                ErrorCode.CERTIFICATION_UPDATE_FORBIDDEN,
                "본인의 인증만 수정 가능합니다."
            );
        }

        // 2) 이미지 URL 결정
        //    - imageKey가 있으면 새로운 URL 조합
        //    - 없으면 기존 URL 유지
        String url = old.getImageUrl();
        if (imageKey != null && !imageKey.isBlank()) {
            url = String.format(
                "https://%s.s3.%s.amazonaws.com/%s",
                bucketName,
                region,
                imageKey
            );
        }

        // 3) DB 업데이트
        certificationMapper.updateCertification(
            certificationId,
            userId,
            comment,
            url
        );

        // 4) 수정된 데이터 다시 조회해 반환
        return getCertificationDetail(userId, certificationId);
    }

	
	// 인증 삭제
	@Override
	@Transactional
	public void deleteCertification(Integer userId,
	                                Integer challengeId,
	                                Integer certificationId) {
	    // 단건 조회
	    CertificationDTO dto = certificationMapper.selectById(certificationId);
	    if (dto == null || !dto.getUserId().equals(userId)) {
	        throw new CustomException(
	            ErrorCode.CERTIFICATION_DELETE_FORBIDDEN,
	            "본인이 등록한 인증만 삭제할 수 있습니다."
	        );
	    }
	    // 실제 삭제 호출
	    certificationMapper.deleteById(certificationId, userId);
	}

}
