package com.hobby.challenge.fobackend.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.hobby.challenge.fobackend.dto.CertificationDTO;
import com.hobby.challenge.fobackend.entity.Certification;

@Mapper
public interface CertificationMapper {
  // 인증 등록
  void insertCertification(Certification certification);
	
  // 페이징된 인증 조회 (기존 findByChallenge 대체)
  List<CertificationDTO> findByChallengeWithPaging(
      @Param("challengeId") Integer challengeId,
      @Param("userId") Integer userId,
      @Param("size") int size,
      @Param("offset") int offset,
      @Param("onlyMine") boolean onlyMine
  );
  
  // 인증 전체 개수 조회 (페이징 계산용)
  int countByChallenge(
      @Param("challengeId") Integer challengeId,
      @Param("userId") Integer userId,
      @Param("onlyMine") boolean onlyMine
  );
  
  // 단건 인증 조회
  CertificationDTO selectById(@Param("certificationId") Integer certificationId);
  
  // 챌린지 삭제시 S3에서 이미지 제거하기 위해 IMAGEURL 조회 
  List<String> findImageUrlsByChallengeId(@Param("challengeId") Integer challengeId);
  
  // 인증 수정
  void updateCertification(@Param("certificationId") Integer certificationId,
          @Param("userId") Integer userId,
          @Param("comment") String comment,
          @Param("imageUrl") String imageUrl);
  
  // 인증 삭제
  void deleteById(@Param("certificationId") Integer certificationId,
		  	      @Param("userId") Integer userId );
  

		  	     
  
}
