package com.gg.server.domain.match.dto;

import com.gg.server.domain.game.data.Game;
import com.gg.server.domain.match.data.RedisMatchTime;
import com.gg.server.domain.slotmanagement.SlotManagement;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MatchStatusDto {
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Boolean isMatched;
    private Boolean isImminent;
    private List<String> myTeam;
    private List<String> enemyTeam;

    public MatchStatusDto(Game game, List<String> myTeam, List<String> enemyTeam, SlotManagement slotManagement) {
        this.startTime = game.getStartTime();
        this.endTime = game.getEndTime();
        this.isMatched = true;
        this.isImminent = game.getStartTime().minusMinutes(slotManagement.getOpenMinute())
                .isBefore(LocalDateTime.now());
        this.myTeam = myTeam;
        this.enemyTeam = enemyTeam;

    }

    public MatchStatusDto(RedisMatchTime redisMatchTime, Integer interval) {
        this.startTime = redisMatchTime.getStartTime();
        this.endTime = redisMatchTime.getStartTime().plusMinutes(interval);
        this.isMatched = false;
        this.isImminent = false;
        this.myTeam = List.of();
        this.enemyTeam = List.of();
    }

    @Override
    public String toString() {
        return "CurrentMatchResponseDto{" +
                "startTime=" + startTime +
                "endTIme=" + endTime +
                ", myTeam=" + myTeam +
                ", enemyTeam=" + enemyTeam +
                ", isMatched=" + isMatched +
                '}';
    }
}
