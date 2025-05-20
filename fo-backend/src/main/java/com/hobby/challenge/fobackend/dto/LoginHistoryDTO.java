package com.hobby.challenge.fobackend.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class LoginHistoryDTO {
    private Integer loginHistoryId;
    private Integer userId;
    private LocalDateTime  loginDate;

}
