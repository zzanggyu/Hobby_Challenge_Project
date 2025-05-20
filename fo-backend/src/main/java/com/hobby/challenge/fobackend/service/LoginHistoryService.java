package com.hobby.challenge.fobackend.service;

import com.hobby.challenge.fobackend.dto.LoginHistoryDTO;
import com.hobby.challenge.fobackend.entity.LoginHistory;

public interface LoginHistoryService {
	LoginHistory recordLoginHistory(LoginHistoryDTO dto);
}
