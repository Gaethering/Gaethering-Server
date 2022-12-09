package com.gaethering.gaetheringserver.board.exception;

import com.gaethering.gaetheringserver.board.exception.errorCode.PostErrorCode;
import lombok.Getter;

@Getter
public class PostException extends RuntimeException {

    private final PostErrorCode postErrorCode;

    protected PostException(PostErrorCode postErrorCode) {
        super(postErrorCode.getMessage());
        this.postErrorCode = postErrorCode;
    }
}
