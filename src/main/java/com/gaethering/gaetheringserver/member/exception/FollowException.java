package com.gaethering.gaetheringserver.member.exception;

import com.gaethering.gaetheringserver.member.exception.errorcode.FollowErrorCode;
import lombok.Getter;

@Getter
public class FollowException extends RuntimeException {

    private final FollowErrorCode errorCode;

    protected FollowException(FollowErrorCode followErrorCode) {
        super(followErrorCode.getMessage());
        this.errorCode = followErrorCode;
    }
}
