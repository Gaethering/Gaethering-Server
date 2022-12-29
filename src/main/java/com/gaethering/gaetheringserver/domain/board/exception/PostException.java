package com.gaethering.gaetheringserver.domain.board.exception;

import com.gaethering.gaetheringserver.core.exception.BusinessException;
import com.gaethering.gaetheringserver.domain.board.exception.errorCode.PostErrorCode;
import lombok.Getter;

@Getter
public class PostException extends BusinessException {

    private final PostErrorCode postErrorCode;

    protected PostException(PostErrorCode postErrorCode) {
        super(postErrorCode);
        this.postErrorCode = postErrorCode;
    }
}