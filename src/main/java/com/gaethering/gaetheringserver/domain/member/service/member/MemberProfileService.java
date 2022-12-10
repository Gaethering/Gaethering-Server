package com.gaethering.gaetheringserver.domain.member.service.member;

import com.gaethering.gaetheringserver.domain.member.dto.profile.OtherProfileResponse;
import com.gaethering.gaetheringserver.domain.member.dto.profile.OwnProfileResponse;

public interface MemberProfileService {

    OwnProfileResponse getOwnProfile(String email);

    OtherProfileResponse getOtherProfile(Long memberId);
}
