package com.gaethering.gaetheringserver.member.exception;

import com.gaethering.gaetheringserver.core.exception.BusinessException;
import com.gaethering.gaetheringserver.member.exception.errorcode.FollowErrorCode;
import lombok.Getter;

@Getter
public class FollowException extends BusinessException {

    private final FollowErrorCode errorCode;

    protected FollowException(FollowErrorCode followErrorCode) {
        super(followErrorCode);
        this.errorCode = followErrorCode;
    }
}
