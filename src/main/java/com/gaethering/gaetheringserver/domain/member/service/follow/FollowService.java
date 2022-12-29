package com.gaethering.gaetheringserver.domain.member.service.follow;

import com.gaethering.gaetheringserver.domain.member.dto.follow.FollowResponse;

import java.util.List;

public interface FollowService {

    boolean createFollow(String followerEmail, Long followeeId);

    List<FollowResponse> getFollowers(Long memberId);

    List<FollowResponse> getFollowees(Long memberId);

    boolean removeFollow(String followerEmail, Long followeeId);
}
