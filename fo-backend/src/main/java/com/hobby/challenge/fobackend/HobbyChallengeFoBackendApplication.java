package com.hobby.challenge.fobackend;

import org.springframework.boot.SpringApplication;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling // 스케줄링 활성화
public class HobbyChallengeFoBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(HobbyChallengeFoBackendApplication.class, args);
		
	}

}
//