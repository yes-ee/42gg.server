package com.gg.server.domain.tournament.data;

import com.gg.server.domain.game.data.Game;
import com.gg.server.utils.annotation.UnitTest;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;

@UnitTest
@DisplayName("TournamentGameUnitTest")
public class TournamentGameUnitTest {

    TournamentGame tournamentGame;

    @Nested
    @DisplayName("UpdateGame")
    class UpdateGame {
      @Test
      @DisplayName("TournamentGame의 게임 업데이트 성공")
      void updateSuccess() {
          //given
          tournamentGame = new TournamentGame();
          Game game = Mockito.mock(Game.class);

          //when
          tournamentGame.updateGame(game);

          //then
          Assertions.assertEquals(game, tournamentGame.getGame());
      }
    }
}
