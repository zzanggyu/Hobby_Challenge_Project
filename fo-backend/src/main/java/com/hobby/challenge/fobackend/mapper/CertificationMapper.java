package com.hobby.challenge.fobackend.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.hobby.challenge.fobackend.dto.CertificationDTO;
import com.hobby.challenge.fobackend.entity.Certification;

@Mapper
public interface CertificationMapper {
	
  List<CertificationDTO> findByChallenge(@Param("challengeId") Integer challengeId);
  
  // 인증 등록
  void insertCertification(Certification certification);
  
}
