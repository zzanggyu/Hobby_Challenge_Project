package com.hobby.challenge.fobackend.service.impl;

import java.util.List;
import org.springframework.stereotype.Service;
import com.hobby.challenge.fobackend.dto.UserRankingDTO;
import com.hobby.challenge.fobackend.mapper.RankingMapper;
import com.hobby.challenge.fobackend.service.RankingService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RankingServiceImpl implements RankingService {
    
    private final RankingMapper rankingMapper;
    
    @Override
    public List<UserRankingDTO> getUserRankingsByCertificationCount(int limit) {
        return rankingMapper.findUsersByCertificationCount(limit);
    }
}