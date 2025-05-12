package com.hobby.challenge.fobackend.entity;

import java.sql.Timestamp;
import java.time.LocalDate;
import lombok.Data;

@Data
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
    private String role; // member, owner, admin
    private Integer points; // 경험치
    private Short level; 
    private Timestamp createdDate;
    private Timestamp modifiedDate;
}
