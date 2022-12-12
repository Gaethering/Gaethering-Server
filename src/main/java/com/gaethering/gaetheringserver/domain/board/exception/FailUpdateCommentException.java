package com.gaethering.gaetheringserver.domain.board.exception;

import com.gaethering.gaetheringserver.domain.board.exception.errorCode.PostErrorCode;

public class FailUpdateCommentException extends PostException{

    public FailUpdateCommentException() {
        super(PostErrorCode.NO_PERMISSION_TO_UPDATE_COMMENT);
    }
}
