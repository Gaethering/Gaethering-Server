package com.gaethering.gaetheringserver.domain.board.exception;

import com.gaethering.gaetheringserver.domain.board.exception.errorCode.PostErrorCode;

public class HeartNotFoundException extends PostException{

    public HeartNotFoundException() {
        super(PostErrorCode.HEART_NOT_FOUND);
    }
}
