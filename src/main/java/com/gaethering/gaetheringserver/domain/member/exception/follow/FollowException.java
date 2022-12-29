package com.gaethering.gaetheringserver.domain.member.exception.follow;

import com.gaethering.gaetheringserver.core.exception.BusinessException;
import com.gaethering.gaetheringserver.domain.member.exception.errorcode.FollowErrorCode;
import lombok.Getter;

@Getter
public class FollowException extends BusinessException {

    private final FollowErrorCode errorCode;

    protected FollowException(FollowErrorCode followErrorCode) {
        super(followErrorCode);
        this.errorCode = followErrorCode;
    }
}
