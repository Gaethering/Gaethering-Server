package com.gaethering.gaetheringserver.domain.board.exception;

import com.gaethering.gaetheringserver.domain.board.exception.errorCode.PostErrorCode;

public class PostImageNotFoundException extends PostException {

    public PostImageNotFoundException() {
        super(PostErrorCode.POST_IMAGE_NOT_FOUND);
    }
}
