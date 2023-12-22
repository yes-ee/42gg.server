package com.gg.server.global.scheduler;

import com.gg.server.domain.tournament.service.TournamentService;
import com.gg.server.global.config.ConstantConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TournamentScheduler extends AbstractScheduler {
    private final TournamentService tournamentService;

    private final ConstantConfig constantConfig;

    public TournamentScheduler(TournamentService tournamentService, ConstantConfig constantConfig) {
        this.tournamentService = tournamentService;
        this.constantConfig = constantConfig;
        this.cron = constantConfig.getTournamentSchedule(); // TODO QA 이후 0 0 0 * * * 로 변경
    }

    @Override
    public Runnable runnable() {
        return () -> {
            log.info("Tournament Scheduler Started");
            tournamentService.startTournament();
        };
    }
}
