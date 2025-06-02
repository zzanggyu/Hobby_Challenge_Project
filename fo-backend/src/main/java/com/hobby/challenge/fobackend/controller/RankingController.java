package com.hobby.challenge.fobackend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hobby.challenge.fobackend.dto.UserRankingDTO;
import com.hobby.challenge.fobackend.service.RankingService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/rankings")
@RequiredArgsConstructor
public class RankingController {
    
    private final RankingService rankingService;
    
    
    // 사용자별 인증 수 랭킹 조회
    @GetMapping("/users")
    public ResponseEntity<List<UserRankingDTO>> getUserRankings(
            @RequestParam(name = "limit", defaultValue = "10") int limit) {
        
        List<UserRankingDTO> rankings = rankingService.getUserRankingsByCertificationCount(limit);
        return ResponseEntity.ok(rankings);
    }
}
