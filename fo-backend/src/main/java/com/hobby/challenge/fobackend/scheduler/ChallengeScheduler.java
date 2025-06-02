package com.hobby.challenge.fobackend.scheduler;

import java.time.LocalDate;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.hobby.challenge.fobackend.entity.Challenge;
import com.hobby.challenge.fobackend.mapper.ChallengeMapper;
import com.hobby.challenge.fobackend.service.ChallengeService;
import com.hobby.challenge.fobackend.service.NotificationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChallengeScheduler {
    
    private final ChallengeService challengeService;
    private final NotificationService notificationService; 
    private final ChallengeMapper challengeMapper; 
    
    // 매일 자정에 만료된 챌린지 논리 삭제
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
    
    // 매일 00시: 챌린지 시작/종료 알림 체크
    @Scheduled(cron = "0 0 0 * * *") // 매일 00:00:00
    public void sendChallengeLifecycleNotifications() {
        try {
            LocalDate today = LocalDate.now();
            LocalDate tomorrow = today.plusDays(1);
            LocalDate yesterday = today.plusDays(1); // 내일 종료하는 챌린지들
            
            // 1 내일 시작하는 챌린지들 "시작 예정" 알림
            List<Challenge> startingSoon = challengeMapper.findChallengesStartingOn(tomorrow);
            for (Challenge challenge : startingSoon) {
                notificationService.createChallengeStartingSoonNotification(challenge.getChallengeId());
                log.info("챌린지 '{}' 시작 예정 알림 발송 (1일 전)", challenge.getTitle());
            }
            
            // 2 오늘 시작하는 챌린지들 "시작됨" 알림
            List<Challenge> startingToday = challengeMapper.findChallengesStartingOn(today);
            for (Challenge challenge : startingToday) {
                notificationService.createChallengeStartedNotification(challenge.getChallengeId());
                log.info("챌린지 '{}' 시작 알림 발송", challenge.getTitle());
            }
            
            // 3 1일 후 종료하는 챌린지들 "종료 예정" 알림
            List<Challenge> endingSoon = challengeMapper.findChallengesEndingOn(yesterday);
            for (Challenge challenge : endingSoon) {
                notificationService.createChallengeEndingSoonNotification(challenge.getChallengeId());
                log.info("챌린지 '{}' 종료 예정 알림 발송 (1일 전)", challenge.getTitle());
            }
            
            // 4 오늘 종료하는 챌린지들 "종료됨" 알림
            List<Challenge> endingToday = challengeMapper.findChallengesEndingOn(today);
            for (Challenge challenge : endingToday) {
                notificationService.createChallengeEndedNotification(challenge.getChallengeId());
                log.info("챌린지 '{}' 종료 알림 발송", challenge.getTitle());
            }
            
        } catch (Exception e) {
            log.error("챌린지 생명주기 알림 발송 중 오류 발생", e);
        }
    }
    
    // 테스트용: 매 1분마다 실행 (개발 중에만 사용)
    // @Scheduled(fixedRate = 60000) // 1분마다
    public void expireOldChallengesForTest() {
        expireOldChallenges();
    }
}
