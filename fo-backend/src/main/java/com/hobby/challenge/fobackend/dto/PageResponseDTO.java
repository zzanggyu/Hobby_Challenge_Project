package com.hobby.challenge.fobackend.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageResponseDTO<T> {
    private int totalCount;    // 전체 아이템 수
    private List<T> items; // 현재 페이지 아이템
}
