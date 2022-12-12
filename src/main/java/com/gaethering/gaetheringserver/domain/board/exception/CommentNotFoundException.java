package com.gaethering.gaetheringserver.domain.board.exception;

import com.gaethering.gaetheringserver.domain.board.exception.errorCode.PostErrorCode;

public class CommentNotFoundException extends PostException{

    public CommentNotFoundException() {
        super(PostErrorCode.COMMENT_NOT_FOUND);
    }
}
