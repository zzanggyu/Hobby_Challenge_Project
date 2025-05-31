package com.hobby.challenge.fobackend.service;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

public interface S3StorageService {
	// 주어진 MultipartFile 을 S3에 업로드하고, 외부에서 접근할 수 있는 URL을 반환
	// file: 업로드할 파일, keyprefix: 버킷 내 저장 경로
	String upload(MultipartFile file, String keyPrefix) throws IOException;
	
	// S3에서 파일 삭제
	void delete(String fileUrl);
}
