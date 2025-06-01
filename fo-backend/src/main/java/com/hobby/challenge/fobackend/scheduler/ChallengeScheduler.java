package com.hobby.challenge.fobackend.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.hobby.challenge.fobackend.service.ChallengeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChallengeScheduler {
    
    private final ChallengeService challengeService;
    
    // 매일 자정에 만료된 챌린지 소프트 삭제
    @Scheduled(cron = "0 0 0 * * *") // 매일 00:00:00
    public void expireOldChallenges() {
        try {
            int expiredCount = challengeService.expireChallenges();
            if (expiredCount > 0) {
                log.info("만료된 챌린지 {}개 처리 완료", expiredCount);
            }
        } catch (Exception e) {
            log.error("만료된 챌린지 처리 중 오류 발생", e);
        }
    }
    
    // 테스트용: 매 1분마다 실행 (개발 중에만 사용)
    // @Scheduled(fixedRate = 60000) // 1분마다
    public void expireOldChallengesForTest() {
        expireOldChallenges();
    }
}
