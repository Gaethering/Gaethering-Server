package com.gaethering.gaetheringserver.member.exception;

import com.gaethering.gaetheringserver.member.exception.errorcode.FollowErrorCode;

public class FollowNotFoundException extends FollowException {

    public FollowNotFoundException() {
        super(FollowErrorCode.FOLLOW_NOT_FOUND);
    }
}
