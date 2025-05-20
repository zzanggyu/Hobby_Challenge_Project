package com.hobby.challenge.fobackend.entity;

import java.sql.Timestamp;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor    // 파라미터 없는 생성자 자동 생성
@AllArgsConstructor   // 모든 필드를 파라미터로 받는 생성자 자동 생성
public class User {
    private Integer userId; // 식별 번호
    private String loginId; // 로그인 아이디
    private String username; // 사용자 본명
    private String password;
    private String email;
    private String nickname;
    private String imageUrl;
    private LocalDate birthDate;
    private String gender; // ENUM은 String으로 받아도 됨
    private String role; // member, admin
    private Integer points; // 경험치
    private Short level; 
    private Timestamp createdDate;
    private Timestamp modifiedDate;
}
