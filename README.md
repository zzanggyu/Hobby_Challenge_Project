# 🎯 HobbyChallenge - 취미 챌린지 플랫폼

> **취미를 기록하고, 함께 성장하는 공간**  
> Vue 3 + Spring Boot 3 + MySQL + JWT 기반의 풀스택 웹 서비스

![Vue.js](https://img.shields.io/badge/Vue.js-4FC08D?style=for-the-badge&logo=vue.js&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-000000?style=for-the-badge&logo=JSON%20web%20tokens&logoColor=white)

## ⚠️ 프로젝트 범위

본 저장소는 **사용자 서비스(FO)** 부분만 포함합니다.
관리자 시스템(BO)은 회사 보안 정책에 따라 별도 관리됩니다.

---

## 📋 프로젝트 소개

**HobbyChallenge**는 개인의 취미 활동을 체계적으로 관리하고, 다른 사용자들과 함께 동기부여를 받을 수 있는 소셜 웹 플랫폼입니다.

### 💡 기획 배경

- **혼자서는 지속하기 어려운 취미 활동** → 커뮤니티와 함께 해결
- **일회성 도전으로 끝나는 문제** → 데일리 인증을 통한 꾸준한 동기부여
- **성취감 부족 문제** → 포인트/레벨 시스템으로 게임화된 경험 제공
- **개인별 관심사 차이** → 카테고리별 관심 챌린지 맞춤 관리

### 🎯 핵심 기능

- ✅ **개인 맞춤 챌린지**: 취미별 카테고리와 개인 관심 챌린지 (최대 10개)
- ✅ **데일리 인증 시스템**: 1일 1회 사진+코멘트 인증, 포인트 지급 (5pt)
- ✅ **커뮤니티 소셜**: 좋아요/댓글, 응원 메시지, 참여자간 상호작용
- ✅ **성장 기록**: 포인트/레벨 시스템, 참여/인증 통계, 개인 성장 추적

---

## 🛠️ 사용 기술

### Frontend (fo-frontend)

```json
"Vue 3.5.13"           // Composition API 기반 SPA
"Vuetify 3.8.0"        // Material Design 컴포넌트 라이브러리
"Vue Router 4.5.1"     // 클라이언트 사이드 라우팅
"Pinia 3.0.2"          // 상태 관리 (Vuex 대체)
"Axios 1.9.0"          // HTTP 클라이언트
"date-fns 4.1.0"       // 날짜 처리 라이브러리
"Vite 6.3.5"           // 빌드 도구
```

### Backend (fo-backend)

```gradle
"Spring Boot 3.5.0"         // REST API 서버 프레임워크
"Spring Security"            // 인증/인가 처리
"MyBatis 3.0.4"             // SQL 매퍼 프레임워크
"MySQL Connector"            // 데이터베이스 드라이버
"JWT (jjwt 0.11.5)"         // 토큰 기반 인증
"Spring Boot Mail"           // 이메일 인증 서비스
"Spring Data Redis"          // 캐시 및 세션 관리
"AWS S3 SDK"                 // 이미지 업로드 스토리지
"Lombok 1.18.30"            // 코드 간소화
```

### Database & Infrastructure

- **MySQL 8.x**: 메인 데이터베이스
- **Redis**: 캐시 및 이메일 인증 토큰 저장
- **AWS S3**: 인증 이미지 파일 저장소

---

## 📁 프로젝트 구조

```
hobby-challenge/
├── fo-backend/                 # Spring Boot 백엔드
│   ├── src/main/java/com/hobby/challenge/fobackend/
│   │   ├── config/            # 설정 클래스 (Security, JWT, CORS)
│   │   ├── controller/        # REST API 컨트롤러
│   │   ├── service/           # 비즈니스 로직 서비스
│   │   ├── mapper/            # MyBatis 매퍼 인터페이스
│   │   ├── dto/               # 데이터 전송 객체
│   │   ├── entity/            # JPA 엔티티
│   │   ├── exception/         # 커스텀 예외 처리
│   │   └── util/              # 유틸리티 클래스
│   ├── src/main/resources/
│   │   ├── mapper/            # MyBatis XML 매퍼
│   │   ├── application.properties  # 설정 파일
│   │   └── static/            # 정적 리소스
│   └── build.gradle           # Gradle 의존성 관리
│
└── fo-frontend/               # Vue.js 프론트엔드
    ├── src/
    │   ├── components/        # 재사용 컴포넌트
    │   │   ├── common/        # 공통 컴포넌트
    │   │   ├── layout/        # 레이아웃 컴포넌트 (Header 등)
    │   │   ├── challenge/     # 챌린지 관련 컴포넌트
    │   │   └── sections/      # 섹션별 컴포넌트
    │   ├── views/             # 페이지 컴포넌트
    │   ├── router/            # Vue Router 설정
    │   ├── stores/            # Pinia 상태 관리
    │   ├── services/          # API 서비스
    │   ├── assets/            # 정적 에셋 (이미지, CSS)
    │   └── utils/             # 유틸리티 함수
    ├── package.json           # npm 의존성 관리
    └── vite.config.js         # Vite 빌드 설정
```

---

## 🗄️ 주요 기능 구현

### 🔐 사용자 인증 시스템

- **JWT 토큰 기반 인증**: Access Token (1시간) + Refresh Token (7일)
- **이메일 인증**: 회원가입, 아이디/비밀번호 찾기 시 Redis 기반 인증코드 (5-10분)
- **Spring Security**: 보안 설정 및 권한 관리
- **비밀번호 암호화**: BCrypt 해싱

### 🎯 챌린지 시스템

- **CRUD 기능**: 생성/조회/수정/삭제 (생성자 권한)
- **참여 관리**: 요청(REQUESTED) → 승인(APPROVED) 플로우
- **관심 챌린지**: 사용자별 최대 10개 제한
- **카테고리별 필터링**: 취미 분야별 검색

### 📸 인증 시스템

- **이미지 업로드**: AWS S3 연동, 최대 5MB 제한
- **일일 제한**: 1일 1회 인증만 가능, 당일 24시까지 수정/삭제
- **포인트 지급**: 인증 완료 시 5pt 자동 지급
- **소셜 기능**: 좋아요(1회 제한), 댓글(200자 제한)

### 📊 관리자 기능

- **사용자 관리**: 조회/수정/정지/삭제, 다중 필터링
- **챌린지 관리**: 활성화/비활성화, 단건/다중 수정/삭제
- **인증 관리**: 코멘트 수정, 이미지 확대 보기
- **카테고리 관리**: CRUD, 사용 중인 카테고리 삭제 방지

---

## 🚀 설치 및 실행

### 📋 사전 요구사항

- **Java 17** 이상
- **Node.js 18** 이상
- **MySQL 8.x**
- **Redis**

### 🖥️ 백엔드 실행

```bash
# 1. 저장소 클론
git clone [https://github.com/zzanggyu/Hobby_Challenge_Project.git]
cd hobby-challenge/fo-backend

# 2. 환경변수 설정 (.env 또는 application.properties)
DB_PASSWORD=your_mysql_password
MAIL_PASSWORD=your_gmail_app_password
JWT_SECRET=your_jwt_secret_key
AWS_ACCESS_KEY_ID=your_aws_access_key
AWS_SECRET_ACCESS_KEY=your_aws_secret_key

# 3. 애플리케이션 실행
./gradlew bootRun
```

서버 실행 후 `http://localhost:8081`에서 확인

### 🎨 프론트엔드 실행

```bash
# 1. 프론트엔드 디렉토리 이동
cd ../fo-frontend

# 2. 의존성 설치
npm install

# 3. 개발 서버 실행
npm run dev
```

개발 서버 실행 후 `http://localhost:5173`에서 확인

---

## 🔍 API 엔드포인트

### 인증 관련

- `POST /api/auth/register` - 회원가입
- `POST /api/auth/login` - 로그인
- `POST /api/auth/find-id` - 아이디 찾기
- `POST /api/auth/reset-password` - 비밀번호 재설정

### 챌린지 관련

- `GET /api/challenges` - 챌린지 목록 조회
- `POST /api/challenges` - 챌린지 생성
- `GET /api/challenges/{id}` - 챌린지 상세 조회
- `PUT /api/challenges/{id}` - 챌린지 수정 (생성자만)

### 인증 관련

- `POST /api/certifications` - 인증 업로드
- `GET /api/certifications` - 인증 목록 조회
- `POST /api/certifications/{id}/like` - 좋아요 토글

---

## 💡 개발 특징

### 보안

- **JWT 기반 Stateless 인증**
- **CORS 설정**: 프론트엔드-백엔드 통신
- **입력 데이터 검증**: Bean Validation 활용
- **SQL Injection 방지**: MyBatis 파라미터 바인딩

### 성능 최적화

- **Redis 캐싱**: 이메일 인증코드, 세션 관리
- **이미지 최적화**: S3 업로드, 파일 크기 제한
- **페이징 처리**: 대용량 데이터 조회 최적화

### 사용자 경험

- **반응형 디자인**: Vuetify Material Design
- **실시간 검증**: 프론트엔드 입력 유효성 검사
- **에러 핸들링**: 통일된 에러 응답 형식

---

## 🐛 트러블슈팅

### 자주 발생하는 이슈

1. **JWT 토큰 만료**: 자동 리프레시 로직 확인
2. **CORS 에러**: `CorsConfig.java` 설정 확인
3. **이미지 업로드 실패**: AWS S3 권한 및 버킷 설정
4. **이메일 발송 실패**: Gmail 앱 비밀번호 설정

---

## 👨‍💻 개발자 정보

- **개발자**: 김현규
- **이메일**: khgg010@naver.com
- **프로젝트 기간**: 2025.05.06 - 2025.06.13

### 연락처

- 010-2716-8327

---
