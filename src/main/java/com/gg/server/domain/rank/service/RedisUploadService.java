package com.gg.server.domain.rank.service;

import com.gg.server.domain.rank.data.Rank;
import com.gg.server.domain.rank.data.RankRepository;
import com.gg.server.domain.rank.redis.RankRedis;
import com.gg.server.domain.rank.redis.RankRedisRepository;
import com.gg.server.domain.rank.redis.RedisKeyManager;
import com.gg.server.domain.season.data.Season;
import com.gg.server.domain.season.data.SeasonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional
public class RedisUploadService {
    private final RankRedisRepository redisRepository;
    private final SeasonRepository seasonRepository;
    private final RankRepository rankRepository;

    @PostConstruct
    public void uploadRedis() {
        Season currentSeason = seasonRepository.findCurrentSeason(LocalDateTime.now())
                .orElseThrow(() -> new NoSuchElementException("현재 시즌이 없습니다."));
        String hashKey = RedisKeyManager.getHashKey(currentSeason.getId());
        if(redisRepository.isEmpty(hashKey))
            upload();
    }

    private void upload() {
        seasonRepository.findAll().forEach(season -> {
            String hashKey = RedisKeyManager.getHashKey(season.getId());
            String zSetKey = RedisKeyManager.getZSetKey(season.getId());
            rankRepository.findAllBySeasonId(season.getId()).forEach(rank -> {
                RankRedis rankRedis = RankRedis.from(rank);
                redisRepository.addRankData(hashKey, rank.getUser().getId(), rankRedis);
                if (rank.getWins() + rankRedis.getLosses() != 0)
                    redisRepository.addToZSet(zSetKey, rank.getUser().getId(), rank.getPpp());
            });
        });
    }
}
