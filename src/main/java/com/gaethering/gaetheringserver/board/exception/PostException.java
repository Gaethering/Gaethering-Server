package com.gaethering.gaetheringserver.board.exception;

import com.gaethering.gaetheringserver.board.exception.errorCode.PostErrorCode;
import com.gaethering.gaetheringserver.core.exception.BusinessException;
import lombok.Getter;

@Getter
public class PostException extends BusinessException {

    private final PostErrorCode postErrorCode;

    protected PostException (PostErrorCode postErrorCode) {
        super(postErrorCode);
        this.postErrorCode = postErrorCode;
    }
}