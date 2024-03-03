package gg.utils.exception.party;

import gg.utils.exception.ErrorCode;
import gg.utils.exception.custom.NotExistException;

public class RoomNotFoundException extends NotExistException {
	public RoomNotFoundException(ErrorCode errorCode) {
		super(errorCode.getMessage(), errorCode);
	}
}
