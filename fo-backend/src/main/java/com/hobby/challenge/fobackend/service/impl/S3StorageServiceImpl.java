package com.hobby.challenge.fobackend.service.impl;

import java.io.IOException;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.hobby.challenge.fobackend.exception.CustomException;
import com.hobby.challenge.fobackend.exception.ErrorCode;
import com.hobby.challenge.fobackend.service.S3StorageService;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchBucketException;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

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

	        try {
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

		        String imageUrl = String.format("https://%s.s3.%s.amazonaws.com/%s", 
		                s3BucketName, s3Region.id(), key);		    
		            return imageUrl;
	        } catch (NoSuchBucketException e) {

	            throw new CustomException(ErrorCode.S3_BUCKET_NOT_FOUND, 
	                "파일 저장소에 접근할 수 없습니다. 관리자에게 문의하세요.");
	            
	        } catch (S3Exception e) {
	            String errorCode = e.awsErrorDetails().errorCode(); 

	            
	            switch (errorCode) {
	                case "AccessDenied":
	                    throw new CustomException(ErrorCode.S3_ACCESS_DENIED, 
	                        "파일 업로드 권한이 없습니다.");
	                case "InvalidRequest":
	                    throw new CustomException(ErrorCode.S3_INVALID_REQUEST, 
	                        "잘못된 파일 업로드 요청입니다.");
	                case "SlowDown":
	                    throw new CustomException(ErrorCode.S3_SLOW_DOWN, 
	                        "서버가 과부하 상태입니다. 잠시 후 다시 시도해주세요.");
	                case "ServiceUnavailable":
	                    throw new CustomException(ErrorCode.S3_SERVICE_UNAVAILABLE,
	                        "파일 저장 서비스가 일시적으로 사용할 수 없습니다.");
	                default:
	                    throw new CustomException(ErrorCode.S3_UPLOAD_FAILED, 
	                        "파일 업로드 중 오류가 발생했습니다: " + e.awsErrorDetails().errorMessage());
	            }   
	        } catch (SdkClientException e) {
	
	            throw new CustomException(ErrorCode.NETWORK_ERROR, 
	                "네트워크 연결에 문제가 있습니다. 잠시 후 다시 시도해주세요.");
	                
	        } catch (Exception e) {

	            throw new CustomException(ErrorCode.FILE_UPLOAD_FAILED, 
	                "파일 업로드에 실패했습니다. 관리자에게 문의하세요.");
	        }
//	        // 최종 파일 URL 반환
//	        return String.format("https://%s.s3.%s.amazonaws.com/%s",
//	            s3BucketName,
//	            s3Region.id(),
//	            key
//	        );
	    }
	    

	    private String extractKeyFromUrl(String url) {
	        // https://bucket.s3.region.amazonaws.com/key 형식에서 key 추출
	        if (url == null) return null;
	        
	        String prefix = String.format("https://%s.s3.%s.amazonaws.com/", 
	            s3BucketName, s3Region.id());
	        
	        if (url.startsWith(prefix)) {
	            return url.substring(prefix.length());
	        }
	        return null;
	    }
	    
	    // s3에서 파일 삭제
	    @Override
	    public void delete(String fileUrl) {
	        try {
	            // URL에서 키 추출
	            String key = extractKeyFromUrl(fileUrl);
	            if (key != null) {
	                s3Client.deleteObject(DeleteObjectRequest.builder()
	                    .bucket(s3BucketName)
	                    .key(key)
	                    .build());
	            }
	        } catch (Exception e) {
	            // 삭제 실패는 로그만 남기고 진행
//	            log.error("파일 삭제를 실패했습니다.: {}", fileUrl, e);
	        }
	    }
}


