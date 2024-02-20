package gg.pingpong.utils.exception.custom;

import gg.pingpong.utils.exception.ErrorCode;

public class PageNotFoundException extends CustomRuntimeException {
	public PageNotFoundException(String message, ErrorCode errorCode) {
		super(message, errorCode);
	}

	public PageNotFoundException() {
		super(ErrorCode.PAGE_NOT_FOUND.getMessage(), ErrorCode.PAGE_NOT_FOUND);
	}
}
