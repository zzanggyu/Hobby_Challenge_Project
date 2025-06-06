package com.hobby.challenge.fobackend.service.impl;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.hobby.challenge.fobackend.dto.CertificationDTO;
import com.hobby.challenge.fobackend.dto.PageResponseDTO;
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
import com.hobby.challenge.fobackend.service.NotificationService;
import com.hobby.challenge.fobackend.service.S3StorageService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CertificationServiceImpl implements CertificationService {

	private final CertificationMapper certificationMapper;
	private final ParticipationMapper participationMapper;
	private final ChallengeMapper challengeMapper;
	private final CertLikeMapper certLikeMapper;
	private final NotificationService notificationService;
	// TODO: 실제 이미지 파일 저장을 위한 서비스 (S3) 사용
	private final S3StorageService s3StorageService; // S3 서비스 주입
	@Value("${aws.s3.bucket-name}")
	private String bucketName;
	@Value("${aws.s3.region}")
	private String region;

	
    // 허용할 이미지 타입
    private static final Set<String> ALLOWED_TYPES = Set.of(
        "image/jpeg", "image/jpg", "image/png", "image/gif"
    );
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 10MB
	

	
	// 인증 등록하기
	@Override
	@Transactional
    public CertificationDTO submitCertification(
            Integer userId,
            Integer challengeId, 
            MultipartFile image,
            String comment) {
        
        //  파일 검증
        validateImageFile(image);
        
        //  참여 승인 확인
        ParticipationResponseDTO participation =
                participationMapper.selectByUserAndChallenge(userId, challengeId);
        if (participation == null || !"APPROVED".equals(participation.getStatus())) {
            throw new CustomException(
                ErrorCode.PARTICIPATION_NOT_APPROVED,
                "인증을 등록하려면 먼저 챌린지 참여 승인을 받아야 합니다."
            );
        }
        
        //  챌린지 기간 검증
        Challenge c = challengeMapper.selectById(challengeId, null);
        LocalDate today = LocalDate.now();
        if (today.isBefore(c.getStartDate()) || today.isAfter(c.getEndDate())) {
            throw new CustomException(
                ErrorCode.CERTIFICATION_INVALID_PERIOD,
                "챌린지 기간 내에만 인증할 수 있습니다."
            );
        }
        
        //  S3에 이미지 업로드
        String imageUrl;
        try {
            imageUrl = s3StorageService.upload(image, "certifications/" + challengeId);
        } catch (IOException e) {
            throw new CustomException(ErrorCode.FILE_UPLOAD_FAILED, "이미지 업로드에 실패했습니다.");
        }
        
        //  DB에 저장
        Certification cert = Certification.builder()
                .participationId(participation.getParticipationId())
                .imageUrl(imageUrl)
                .comment(comment)
                .likeCount(0)
                .createdBy(userId)
                .createdDate(LocalDateTime.now())
                .build();
        
        try {
            certificationMapper.insertCertification(cert);
        } catch (DuplicateKeyException e) {
            // S3에 업로드된 파일 삭제 (롤백)
            s3StorageService.delete(imageUrl);
            throw new CustomException(
                ErrorCode.DUPLICATE_CERTIFICATION,
                "오늘은 이미 인증을 등록했습니다."
            );
        }
        
        // 6) DTO 반환
        return certificationMapper.selectById(cert.getCertificationId());
    }
	
	// 인증내역 가져오기
	@Override
	public PageResponseDTO<CertificationDTO> getCertifications(
	        Integer userId, 
	        Integer challengeId, 
	        int size, 
	        int offset, 
	        boolean onlyMine) {
	    
	    // 챌린지가 존재하고 삭제되지 않았는지만 확인
	    Challenge challenge = challengeMapper.selectById(challengeId, null);
	    if (challenge == null || Boolean.TRUE.equals(challenge.getIsDeleted())) {
	        throw new CustomException(ErrorCode.CHALLENGE_NOT_FOUND, "존재하지 않거나 삭제된 챌린지입니다.");
	    }
	    
	    // 페이징된 인증 목록 조회
	    List<CertificationDTO> items = certificationMapper.findByChallengeWithPaging(
	        challengeId, userId, size, offset, onlyMine
	    );
	    
	    // 전체 개수 조회 (페이징 계산용)
	    int totalCount = certificationMapper.countByChallenge(challengeId, userId, onlyMine);
	    
	    // PageResponseDTO로 반환
	    return new PageResponseDTO<>(totalCount, items);
	}
	
    // 파일 검증 
	private void validateImageFile(MultipartFile file) {
	    // 1. 기본 파일 존재 여부 검사 (가장 먼저!)
	    if (file == null || file.isEmpty()) {
	        throw new CustomException(ErrorCode.FILE_REQUIRED, "이미지 파일은 필수입니다.");
	    }

	    // 2. 파일 크기 검증
	    if (file.getSize() > MAX_FILE_SIZE) {
	        throw new CustomException(ErrorCode.FILE_TOO_LARGE, "파일 크기는 최대 5MB까지 가능합니다.");
	    }

	    // 3. MIME 타입 기본 검증
	    String contentType = file.getContentType();
	    if (contentType == null || !ALLOWED_TYPES.contains(contentType.toLowerCase())) {
	        throw new CustomException(
	            ErrorCode.INVALID_FILE_TYPE,
	            "지원하지 않는 이미지 형식입니다. JPG, PNG, GIF만 업로드하세요."
	        );
	    }

	    // 4. 파일명 검증 (경로 순회 공격 방지)
	    String filename = file.getOriginalFilename();
	    if (filename != null && (filename.contains("..") || filename.contains("/") || filename.contains("\\"))) {
	        throw new CustomException(ErrorCode.INVALID_FILE_NAME, "유효하지 않은 파일명입니다.");
	    }

	    // 5. 실제 파일 내용 검증 (MIME 타입 스푸핑 방지)
	    try (InputStream inputStream = file.getInputStream()) {
	        BufferedImage image = ImageIO.read(inputStream);
	        if (image == null) {
	            throw new CustomException(ErrorCode.INVALID_FILE_TYPE, "유효하지 않은 이미지 파일입니다.");
	        }
	        
	        // 6. 이미지 크기 제한
	        if (image.getWidth() > 4000 || image.getHeight() > 4000) {
	            throw new CustomException(ErrorCode.IMAGE_TOO_LARGE, "이미지 해상도가 너무 큽니다. (최대 4000x4000)");
	        }
	        
	    } catch (IOException e) {
	        throw new CustomException(ErrorCode.FILE_READ_ERROR, "파일을 읽는 중 오류가 발생했습니다.");
	    }
	}
	
	// 인증 상세 가져오기
    @Override
    public CertificationDTO getCertificationDetail(Integer userId, Integer certificationId) {
        CertificationDTO dto = certificationMapper.selectById(certificationId);
        if (dto==null) throw new CustomException(ErrorCode.NOT_FOUND_CERTIFICATION, "인증을 찾을 수 없습니다.");
        
        // 좋아요 여부 추가 
        if (userId != null) {
            boolean isLiked = certLikeMapper.isLiked(certificationId, userId) > 0;
            dto.setLikedByMe(isLiked);  // DTO에 좋아요 여부 설정
        }
        // 접근 권한 추가 검증 가능
        return dto;
    }


	
	// 좋아요 토글, 알림
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
            
            // 알림 생성: 인증 작성자에게 좋아요 알림 (본인 좋아요 제외)
            if (!cert.getUserId().equals(userId)) {
                notificationService.createNewLikeNotification(
                    cert.getUserId(), 
                    certificationId,
                    userId // 좋아요 누른 사람
                );
            }
            
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
        MultipartFile image,
        String comment
    ) {
        // 1) 기존 인증 조회 및 권한 확인
        CertificationDTO old = getCertificationDetail(userId, certificationId);
        if (!old.getUserId().equals(userId)) {
            throw new CustomException(
                ErrorCode.CERTIFICATION_UPDATE_FORBIDDEN,
                "본인의 인증만 수정 가능합니다."
            );
        }
        
        // 2) 새 이미지가 있으면 업로드
        String imageUrl = old.getImageUrl(); // 기본값은 기존 URL
        if (image != null && !image.isEmpty()) {
            validateImageFile(image);
            
            try {
                // 새 이미지 업로드
                String newUrl = s3StorageService.upload(image, "certifications/" + challengeId);
                
                // 기존 이미지 삭제
                if (old.getImageUrl() != null) {
                    s3StorageService.delete(old.getImageUrl());
                }
                
                imageUrl = newUrl;
            } catch (IOException e) {
                throw new CustomException(ErrorCode.FILE_UPLOAD_FAILED, "이미지 업로드에 실패했습니다.");
            }
        }
        
        // 3) DB 업데이트
        certificationMapper.updateCertification(
            certificationId, userId, comment, imageUrl
        );
        
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
