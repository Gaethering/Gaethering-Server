package com.gaethering.gaetheringserver.member.service;

import com.gaethering.gaetheringserver.member.dto.OtherProfileResponse;
import com.gaethering.gaetheringserver.member.dto.OwnProfileResponse;

public interface MemberProfileService {

    OwnProfileResponse getOwnProfile(String email);

    OtherProfileResponse getOtherProfile(Long memberId);
}
