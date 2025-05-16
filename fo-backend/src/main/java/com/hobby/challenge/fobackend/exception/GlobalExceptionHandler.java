package com.hobby.challenge.fobackend.exception;

import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.hobby.challenge.fobackend.dto.ErrorResponseDTO;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice // RestController에서 던져진 예외를 전역에서 잡음 @ControllerAdvice + @ResponseBody
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler{
	
	// CustomException 타입 예외 발생 시 메서드 실행	
	@ExceptionHandler(CustomException.class)
	public ResponseEntity<ErrorResponseDTO> handleCustom(CustomException ex, // 던져진 예외 객체 받음
			HttpServletRequest req){ // 요청 URL, 헤더, 파라미터 등 원본 요청 정보 얻을 때 사용
		// ErrorResponseDTO 생성
        ErrorResponseDTO body = new ErrorResponseDTO(
            ex.getErrorCode().getCode(), // 400, 4001, 4002 커스텀 코드
            ex.getErrorCode().getMessage(), // ErrorCode에 정의된 enum 메세지
            ex.getDetail() // 서비스에서 던질 때 전달한 상세 정보
        );
        // HTTP 상태는 코드 앞 3자리로 
        int status = Integer.parseInt(ex.getErrorCode().getCode().substring(0,3));
        return ResponseEntity.status(status).body(body); // HTTP 상태와 body에 ErrorResponseDTO(json) 담아 보내기
	}
	
	// @Valid 검증이 실패했을 때 자동으로 호출
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid( 
			MethodArgumentNotValidException ex, // @Valid가 붙은 DTO가 들어왔을 때 검증 실패 정보 담겨있는 예외 객체
			HttpHeaders headers, // 스프링이 기본적으로 제공하는 HTTP 응답 헤더
		    HttpStatusCode status, // 검증 실패시 400 bad reques
		    WebRequest request // HTTP 요청 정보에 접근할 수 있는 추상화 객체
	){ 
		
	    // 필드별 에러 메시지 수집
	    Map<String,String> errors = ex.getBindingResult().getFieldErrors().stream()
	      .collect(Collectors.toMap(
	        FieldError::getField, // key: 검증 실패한 필드 이름
	        FieldError::getDefaultMessage, // value: 어노테이션에 지정한 오류 메세지
	        (msg1, msg2) -> msg1 + ", " + msg2 // 같은 필드에 오류가 여러 개면 메세지를 조합
	      ));

	    ErrorResponseDTO body = new ErrorResponseDTO(
	      "4000", // 검증 실패 전용 코드
	      "입력값 검증에 실패했습니다.", // 사용자에게 보여줄 메세지
	      errors.toString() // detail: "{field1=메세지1, field2=메세지2} 형식
	    );
	    return new ResponseEntity<>(body, headers, status); // ResponseEntity로 감싸서 반환 header, status, body
	}
	
	// 미처리 예외 방지 CustomException에서 못잡은 에러 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleAll(Exception ex) {
    	// 에러 응답 DTO 생성
        ErrorResponseDTO body = new ErrorResponseDTO(
            "5000", // 커스텀 에러 코드: 서버 내부 오류
            "서버 오류가 발생했습니다.", // 사용자에게 보여줄 기본 메세지
            ex.getMessage() //실제 예외 메세지를 detail에 담아 디버깅에 활용
        );
        return ResponseEntity.status(500).body(body); // HTTP 500 내부 서버 에러 응답
    }
}
