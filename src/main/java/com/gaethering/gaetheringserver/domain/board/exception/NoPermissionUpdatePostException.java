package com.gaethering.gaetheringserver.domain.board.exception;

import com.gaethering.gaetheringserver.domain.board.exception.errorCode.PostErrorCode;

public class NoPermissionUpdatePostException extends PostException {

	public NoPermissionUpdatePostException() {
		super(PostErrorCode.NO_PERMISSION_TO_UPDATE_POST);
	}
}
