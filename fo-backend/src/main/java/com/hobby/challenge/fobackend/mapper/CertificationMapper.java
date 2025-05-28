package com.hobby.challenge.fobackend.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.hobby.challenge.fobackend.dto.CertificationDTO;
import com.hobby.challenge.fobackend.entity.Certification;

@Mapper
public interface CertificationMapper {
  // 인증 조회 
  List<CertificationDTO> findByChallenge(@Param("challengeId") Integer challengeId);
  
  // 단건 인증 조회
  CertificationDTO selectById(@Param("certificationId") Integer certificationId);
  
  // 인증 등록
  void insertCertification(Certification certification);
  
  // 인증 수정
  void updateCertification(@Param("certificationId") Integer certificationId,
          @Param("userId") Integer userId,
          @Param("comment") String comment,
          @Param("imageUrl") String imageUrl);
  
  // 인증 삭제
  void deleteById(@Param("certificationId") Integer certificationId,
		  	      @Param("userId") Integer userId );
  

		  	     
  
}
