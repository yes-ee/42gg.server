package com.gg.server.domain.match.service;

import com.gg.server.domain.game.data.Game;
import com.gg.server.domain.game.data.GameRepository;
import com.gg.server.domain.game.exception.GameAlreadyExistException;
import com.gg.server.domain.game.type.StatusType;
import com.gg.server.domain.match.dto.GameAddDto;
import com.gg.server.domain.match.exception.EnrolledSlotException;
import com.gg.server.domain.match.exception.SlotNotFoundException;
import com.gg.server.domain.noti.service.NotiService;
import com.gg.server.domain.slotmanagement.SlotManagement;
import com.gg.server.domain.slotmanagement.data.SlotManagementRepository;
import com.gg.server.domain.team.data.Team;
import com.gg.server.domain.team.data.TeamRepository;
import com.gg.server.domain.team.data.TeamUser;
import com.gg.server.domain.team.data.TeamUserRepository;
import com.gg.server.domain.user.User;
import com.gg.server.domain.user.UserRepository;
import com.gg.server.domain.user.dto.UserDto;
import com.gg.server.domain.user.exception.UserNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GameUpdateService {
    private final GameRepository gameRepository;
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final TeamUserRepository teamUserRepository;
    private final SlotManagementRepository slotManagementRepository;
    private final NotiService notiService;

    public void make(GameAddDto addDto) {
        SlotManagement slotManagement = slotManagementRepository.findCurrent(LocalDateTime.now())
                .orElseThrow(SlotNotFoundException::new);
        Game game = new Game(addDto, slotManagement.getGameInterval());
        gameRepository.save(game);
        Team enemyTeam =  new Team(game, -1, false);
        Team myTeam = new Team(game, -1, false);
        List<Team> matchPair = List.of(enemyTeam, myTeam);
        teamRepository.saveAll(matchPair);
        User playerUser = userRepository.findById(addDto.getPlayerId()).orElseThrow(UserNotFoundException::new);
        User enemyUser = userRepository.findById(addDto.getEnemyId()).orElseThrow(UserNotFoundException::new);
        TeamUser myTeamUser = new TeamUser(myTeam, playerUser);
        TeamUser enemyTeamUser = new TeamUser(enemyTeam, enemyUser);
        List<TeamUser> matchTeamUser = List.of(enemyTeamUser, myTeamUser);
        teamUserRepository.saveAll(matchTeamUser);
        notiService.createMatched(playerUser, addDto.getStartTime());
        notiService.createMatched(enemyUser, addDto.getStartTime());
    }

    public void delete(Game game, UserDto userDto) {
        List<User> enemyTeam = userRepository.findEnemyByGameAndUser(game.getId(), userDto.getId());
        enemyTeam.forEach(enemy -> notiService.createMatchCancel(enemy, game.getStartTime()));
        gameRepository.delete(game);
    }
}