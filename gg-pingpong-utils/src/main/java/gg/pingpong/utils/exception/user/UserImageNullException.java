package gg.pingpong.utils.exception.user;

import gg.pingpong.utils.exception.ErrorCode;
import gg.pingpong.utils.exception.custom.FileException;

public class UserImageNullException extends FileException {
	public UserImageNullException() {
		super("이미지 파일이 없습니다.", ErrorCode.USER_IMAGE_NOT_FOUND);
	}
}
