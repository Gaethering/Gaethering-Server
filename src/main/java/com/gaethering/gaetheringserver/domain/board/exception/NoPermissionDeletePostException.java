package com.gaethering.gaetheringserver.domain.board.exception;

import com.gaethering.gaetheringserver.domain.board.exception.errorCode.PostErrorCode;

public class NoPermissionDeletePostException extends PostException {

	public NoPermissionDeletePostException() {
		super(PostErrorCode.NO_PERMISSION_TO_DELETE_POST);
	}
}
