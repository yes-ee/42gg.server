package com.gg.server.domain.tournament.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.gg.server.domain.tournament.dto.TournamentListResponseDto;
import com.gg.server.domain.tournament.dto.TournamentResponseDto;
import com.gg.server.domain.tournament.type.TournamentStatus;
import com.gg.server.domain.tournament.type.TournamentType;
import com.gg.server.domain.user.data.User;
import com.gg.server.domain.user.type.RacketType;
import com.gg.server.domain.user.type.RoleType;
import com.gg.server.domain.user.type.SnsType;
import com.gg.server.global.security.jwt.utils.AuthTokenProvider;
import com.gg.server.utils.TestDataUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@RequiredArgsConstructor
@Slf4j
public class TournamentFindControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    TestDataUtils testDataUtils;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    AuthTokenProvider tokenProvider;

    List<TournamentResponseDto> tournamentList;
    String accessToken;

    User tester;

    @BeforeEach
    void beforeEach() {
        tester = testDataUtils.createNewUser("findControllerTester", "findControllerTester", RacketType.DUAL, SnsType.SLACK, RoleType.ADMIN);
        accessToken = tokenProvider.createToken(tester.getId());
        tournamentList = testDataUtils.makeTournamentList();
    }

    @Nested
    @DisplayName("토너먼트_리스트_조회")
    class findTournamentTest {

        @Test
        @DisplayName("전체_조회")
        public void getTournamentList() throws Exception {
            // given
            int page = 2;
            int size = 20;
            String url = "/pingpong/tournament/?page=" + page + "&size=" + size;

            // when
            String contentAsString = mockMvc.perform(get(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();
            TournamentListResponseDto resp = objectMapper.readValue(contentAsString, TournamentListResponseDto.class);

            // then
            List<TournamentResponseDto> tournamentInfoList = resp.getTournaments();
            for (int i = 0; i < tournamentInfoList.size(); i++) {
                Long tournamentId = tournamentInfoList.get(i).getTournamentId();
                TournamentResponseDto tournamentResponseDto = tournamentList.stream().filter(t -> t.getTournamentId().equals(tournamentId)).findFirst().orElse(null);
                if (tournamentResponseDto != null) {
                    assertThat(tournamentInfoList.get(i).getTitle()).isEqualTo(tournamentResponseDto.getTitle());
                    assertThat(tournamentInfoList.get(i).getContents()).isEqualTo(tournamentResponseDto.getContents());
                    assertThat(tournamentInfoList.get(i).getType()).isEqualTo(tournamentResponseDto.getType());
                    assertThat(tournamentInfoList.get(i).getStatus()).isEqualTo(tournamentResponseDto.getStatus());
                    assertThat(tournamentInfoList.get(i).getWinnerIntraId()).isEqualTo(tournamentResponseDto.getWinnerIntraId());
                    assertThat(tournamentInfoList.get(i).getWinnerImageUrl()).isEqualTo(tournamentResponseDto.getWinnerImageUrl());
                    assertThat(tournamentInfoList.get(i).getPlayer_cnt()).isEqualTo(tournamentResponseDto.getPlayer_cnt());
                }
            }
        }

        @Test
        @DisplayName("status별_조회")
        public void getTournamentListByStatus() throws Exception {

            // given
            int page = 1;
            int size = 10;
            String url = "/pingpong/tournament/?page=" + page + "&size=" + size + "&status=" + TournamentStatus.BEFORE.getCode();

            // when
            String contentAsString = mockMvc.perform(get(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();
            TournamentListResponseDto resp = objectMapper.readValue(contentAsString, TournamentListResponseDto.class);

            // then
            List<TournamentResponseDto> tournamentInfoList = resp.getTournaments();
            for (TournamentResponseDto responseDto : tournamentInfoList) {
                assertThat(responseDto.getStatus()).isEqualTo(TournamentStatus.BEFORE);
            }
        }

        @Test
        @DisplayName("type별_조회")
        public void getTournamentListByType() throws Exception {

            // given
            int page = 1;
            int size = 10;
            String url = "/pingpong/tournament/?page=" + page + "&size=" + size + "&type=" + TournamentType.ROOKIE.getCode();

            // when
            String contentAsString = mockMvc.perform(get(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();
            TournamentListResponseDto resp = objectMapper.readValue(contentAsString, TournamentListResponseDto.class);

            // then
            List<TournamentResponseDto> tournamentInfoList = resp.getTournaments();
            for (TournamentResponseDto responseDto : tournamentInfoList) {
                assertThat(responseDto.getType()).isEqualTo(TournamentType.ROOKIE);
            }
        }

        @Test
        @DisplayName("type과 status 별 조회")
        public void getTournamentListByTypeAndStatus() throws Exception {
            // given
            int page = 1;
            int size = 10;
            String url = "/pingpong/tournament/?page=" + page + "&size=" + size + "&type=" + TournamentType.ROOKIE.getCode() + "&status=" + TournamentStatus.BEFORE.getCode();

            // when
            String contentAsString = mockMvc.perform(get(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();
            TournamentListResponseDto resp = objectMapper.readValue(contentAsString, TournamentListResponseDto.class);

            // then
            List<TournamentResponseDto> tournamentInfoList = resp.getTournaments();
            for (TournamentResponseDto responseDto : tournamentInfoList) {
                assertThat(responseDto.getType()).isEqualTo(TournamentType.ROOKIE);
                assertThat(responseDto.getStatus()).isEqualTo(TournamentStatus.BEFORE);
            }
        }

        @Test
        @DisplayName("잘못된 type")
        public void wrongType() throws Exception {
            // given
            int page = 1;
            int size = 10;
            String url = "/pingpong/tournament/?page=" + page + "&size=" + size + "&type=" + "rookie123" + "&status=" + TournamentStatus.BEFORE.getCode();

            // when
            String contentAsString = mockMvc.perform(get(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                    .andExpect(status().isBadRequest())
                    .andReturn().getResponse().getContentAsString();

            // then
            log.info(contentAsString);
        }

        @Test
        @DisplayName("잘못된 status")
        public void wrongStatus() throws Exception {
            // given
            int page = 1;
            int size = 10;
            String url = "/pingpong/tournament/?page=" + page + "&size=" + size + "&type=" + TournamentType.ROOKIE.getCode() + "&status=" + "wrongStatus";

            // when
            String contentAsString = mockMvc.perform(get(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                    .andExpect(status().isBadRequest())
                    .andReturn().getResponse().getContentAsString();

            // then
            log.info(contentAsString);
        }
    }
}