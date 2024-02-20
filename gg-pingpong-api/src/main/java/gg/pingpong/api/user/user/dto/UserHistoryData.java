package gg.pingpong.api.user.user.dto;

import java.time.LocalDateTime;

import com.gg.server.data.game.PChange;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserHistoryData {
	private int ppp;
	private LocalDateTime date;

	public UserHistoryData(PChange pChange) {
		this.ppp = pChange.getPppResult();
		this.date = pChange.getCreatedAt();
	}
}
