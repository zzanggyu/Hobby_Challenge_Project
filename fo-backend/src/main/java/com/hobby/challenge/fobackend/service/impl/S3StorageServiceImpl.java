package com.hobby.challenge.fobackend.service.impl;

import java.io.IOException;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.hobby.challenge.fobackend.service.S3StorageService;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;  
@Service
@RequiredArgsConstructor
public class S3StorageServiceImpl implements S3StorageService{
	  private final S3Client s3Client;
	    private final String s3BucketName;
	    private final Region s3Region;

	    @Override
	    public String upload(MultipartFile file, String keyPrefix) throws IOException {
	        // 파일 이름 충돌 방지를 위해 UUID 추가
	        String key = keyPrefix + "/" + UUID.randomUUID() + "_" + file.getOriginalFilename();

	        // S3로 업로드
	        s3Client.putObject(
	            PutObjectRequest.builder()
	                .bucket(s3BucketName)
	                .key(key)
	                .acl(ObjectCannedACL.PUBLIC_READ) // 공개 읽기 권한
	                .contentType(file.getContentType())
	                .build(),
	            RequestBody.fromBytes(file.getBytes())
	        );

	        // 최종 파일 URL 반환
	        return String.format("https://%s.s3.%s.amazonaws.com/%s",
	            s3BucketName,
	            s3Region.id(),
	            key
	        );
	    }

}
