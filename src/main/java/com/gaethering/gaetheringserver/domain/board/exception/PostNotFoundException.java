package com.gaethering.gaetheringserver.domain.board.exception;

import com.gaethering.gaetheringserver.domain.board.exception.errorCode.PostErrorCode;

public class PostNotFoundException extends PostException {

    public PostNotFoundException() {
        super(PostErrorCode.POST_NOT_EXIST);
    }
}
