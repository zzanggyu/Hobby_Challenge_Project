package com.hobby.challenge.fobackend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
public class S3Config {

    // application.properties 의 aws.s3.credentials.access-key
    @Value("${aws.s3.credentials.access-key}")
    private String accessKey;

    // application.properties 의 aws.s3.credentials.secret-key
    @Value("${aws.s3.credentials.secret-key}")
    private String secretKey;

    // application.properties 의 aws.s3.region
    @Value("${aws.s3.region}")
    private String region;

    // application.properties 의 aws.s3.bucket-name
    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    

    // AWS 자격증명(AccessKey/SecretKey) 프로바이더 생성.S3Client 와 S3Presigner 에서 동일하게 사용
    private StaticCredentialsProvider awsCredentialsProvider() {
        AwsBasicCredentials creds = AwsBasicCredentials.create(accessKey, secretKey);
        return StaticCredentialsProvider.create(creds);
    }

    // S3Client 빈 생성파일 업로드, 삭제, 메타데이터 조회 등 모든 S3 조작에 사용
    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
                // 리전 설정
                .region(Region.of(region))
                // 자격증명 프로바이더 설정
                .credentialsProvider(awsCredentialsProvider())
                .build();
    }
    
    //  S3Presigner 빈 생성 presign URL을 만들 때 사용
    @Bean
    public S3Presigner s3Presigner() {
        return S3Presigner.builder()
            .region(Region.of(region))
            .credentialsProvider(
                StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(accessKey, secretKey)
                )
            )
            .build();
    }

    // 버킷 이름을 다른 서비스에서 주입받을 수 있게 빈으로 등록
    @Bean
    public String s3BucketName() {
        return bucketName;
    }
    
    @Bean
    public Region s3Region() {
        return Region.of(region);
    }
}
