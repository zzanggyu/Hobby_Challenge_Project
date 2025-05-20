package com.hobby.challenge.fobackend.service;

import com.hobby.challenge.fobackend.dto.CreateChallengeRequestDTO;
import com.hobby.challenge.fobackend.entity.Challenge;

public interface ChallengeService {
	
	Challenge createChallenge(CreateChallengeRequestDTO dto, Integer createdBy);
}
