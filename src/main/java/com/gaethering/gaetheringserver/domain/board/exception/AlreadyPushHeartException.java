package com.gaethering.gaetheringserver.domain.board.exception;

import com.gaethering.gaetheringserver.domain.board.exception.errorCode.PostErrorCode;

public class AlreadyPushHeartException extends PostException{

    public AlreadyPushHeartException() {
        super(PostErrorCode.HEART_ALREADY_PUSH);
    }
}
