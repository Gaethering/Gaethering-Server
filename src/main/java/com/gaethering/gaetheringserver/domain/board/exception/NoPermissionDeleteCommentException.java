package com.gaethering.gaetheringserver.domain.board.exception;

import com.gaethering.gaetheringserver.domain.board.exception.errorCode.PostErrorCode;

public class NoPermissionDeleteCommentException extends PostException {

    public NoPermissionDeleteCommentException() {
        super(PostErrorCode.NO_PERMISSION_TO_DELETE_COMMENT);
    }
}
