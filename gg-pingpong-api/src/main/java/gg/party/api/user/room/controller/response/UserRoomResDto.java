package gg.party.api.user.room.controller.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Getter
public class UserRoomResDto {
	private Long roomUserId;
	private String nickname;

	public UserRoomResDto(Long id, String nickname) {
		this.roomUserId = id;
		this.nickname = nickname;
	}
}