package com.gaethering.gaetheringserver.member.service.member;

import com.gaethering.gaetheringserver.member.dto.profile.OtherProfileResponse;
import com.gaethering.gaetheringserver.member.dto.profile.OwnProfileResponse;

public interface MemberProfileService {

    OwnProfileResponse getOwnProfile(String email);

    OtherProfileResponse getOtherProfile(Long memberId);
}
