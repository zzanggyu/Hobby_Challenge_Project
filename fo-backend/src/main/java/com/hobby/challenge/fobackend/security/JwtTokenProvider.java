package com.hobby.challenge.fobackend.security;

import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

// JWT 생성, 파싱, 유효성 검사를 담당한다.
@Component // Spring 컨테이너가 클래스를 빈으로 관리
public class JwtTokenProvider {
	private final Key key;
	private final long validityInMilliseconds;
    // 리프레시 만료(ms)
    private final long refreshValidityInMilliseconds;
	
	// application.properties의 jwt.secret(서명키), jwt.expiration(토큰 유효시간)을 가져와 초기화
	public JwtTokenProvider(@Value("${jwt.secret}") String secret, // @Value application.properties에 있는 값을 읽어서 주입
			@Value("${jwt.expiration}") long validityInMilliseconds,
			@Value("${jwt.refreshExpiration}") long refreshValidityInMilliseconds) {
		
		byte[] keyBytes = Decoders.BASE64.decode(secret); // 인코딩된 키를 바이트 배열로 디코딩
		this.key = Keys.hmacShaKeyFor(keyBytes); // 아래 주석 부분처럼 동작
//		if (keyBytes.length < 32) {
//		    throw new IllegalArgumentException("HS256에 필요한 최소 키 길이는 32바이트(256비트)입니다.");
//		}
//		SecretKey secretKey = new SecretKeySpec(keyBytes, "HmacSHA256");
//		return secretKey;
		this.validityInMilliseconds = validityInMilliseconds; // 만료 기간 저장
		this.refreshValidityInMilliseconds = refreshValidityInMilliseconds;
	}
	
	// 로그인 아이디(sub) 클레임만 담아 토큰 생성. iat,exp를 자동 세팅하고 HS256으로 서명한다.
	public String createToken(String loginId) {
		Date now = new Date(); // 토큰 생성 시점
		Date expiry = new Date(now.getTime() + validityInMilliseconds); // 토큰 만료 시점
		return Jwts.builder() 
				.setSubject(loginId)  // 클레임 설정 및 서명 후 직렬화하여 문자열로 반환
				.setIssuedAt(now) // iat(issued at) 클레임: 토큰 발급 시각
				.setExpiration(expiry) // exp(expiration) 클레임: 토큰 만료 시각
				.signWith(key, SignatureAlgorithm.HS256) // 서명 설정: 이전에 만든 ScretKey와 HS256 알고리즘
				.compact(); // 토큰 직렬화: "header.payload.signature" 형태로 반환. 클라이언트에 전달할 JWT토큰
	}
	
    // Refresh Token 생성
    public String createRefreshToken(String loginId) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + refreshValidityInMilliseconds);
        return Jwts.builder()
                .setSubject(loginId)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
	
	// 토큰에서 서명 검증 후 subject(loginId) 반환 서명일 틀리거나 만료되면 예외가 발생함
	public String getLoginId(String token) {
		return Jwts.parserBuilder()
				.setSigningKey(key) // 이전에 만든 ScretKey 설정
				.build() // JwtParse 인스턴스 생성
				.parseClaimsJws(token) // 토큰 문자열 파싱 / 서명 만료 검증
				.getBody() // 파싱된 JWS 객체에서 Claims(payload) 꺼내기
				.getSubject(); // sub 클레임 반환
	}
	
	// 토큰의 서명 유효성, 만료 등을 검증. 정상이면 true, 아니면 false
	// 검증 실패 때마다 예외를 던지면 제어 흐름이 복잡해지기 때문에 try / catch로 (true/false)처리
	public boolean validateToken(String token) {
		try {
			// 서명 검증과 만료 검사 동시에 시행
			Jwts.parserBuilder()
				.setSigningKey(key)
				.build()
				.parseClaimsJws(token);
			return true; // 정상 토큰
			
		}catch(JwtException // 서명 불일치, 만료, 잘못된 포맷 (ExpiredJwtException, SignatureException)
				| IllegalArgumentException e) { // null이나 빈 문자열 토큰 처리
			return false; // 서명 오류, 만료 등 모든 예외는 false 처리
		}
	}

}
