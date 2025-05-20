package com.hobby.challenge.fobackend.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hobby.challenge.fobackend.dto.LoginHistoryDTO;
import com.hobby.challenge.fobackend.entity.LoginHistory;
import com.hobby.challenge.fobackend.mapper.LoginHistoryMapper;
import com.hobby.challenge.fobackend.service.LoginHistoryService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LoginHistoryServiceImpl implements LoginHistoryService {
	private final LoginHistoryMapper loginHistoryMapper;
	
	// 로그인 이력 저장
    @Override
    @Transactional
    public LoginHistory recordLoginHistory(LoginHistoryDTO dto) {
        // DTO → Entity 변환
        LoginHistory history = LoginHistory.builder()
            .userId(dto.getUserId())
            .loginDate(dto.getLoginDate())
            .build();

        // DB에 저장
        loginHistoryMapper.insertLoginHistory(history);

        // DB에서 생성된 PK, 타임스탬프가 반영된 객체를 반환
        return history;
	
    }
}
