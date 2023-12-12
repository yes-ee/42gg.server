package com.gg.server.domain.user.dto;

import com.gg.server.domain.game.type.Mode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserLiveResponseDto {
    private int notiCount;
    private String event;
    private Mode currentMatchMode;
    private Long gameId;
}
