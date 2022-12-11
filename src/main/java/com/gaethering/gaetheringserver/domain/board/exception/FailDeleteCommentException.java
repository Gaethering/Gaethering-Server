package com.gaethering.gaetheringserver.domain.board.exception;

import com.gaethering.gaetheringserver.domain.board.exception.errorCode.PostErrorCode;

public class FailDeleteCommentException extends PostException {

    public FailDeleteCommentException() {
        super(PostErrorCode.CANNOT_DELETE_COMMENT);
    }
}
