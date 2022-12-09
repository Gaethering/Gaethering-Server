package com.gaethering.gaetheringserver.member.service;

import com.gaethering.gaetheringserver.member.dto.FollowResponse;
import java.util.List;

public interface FollowService {

    boolean createFollow(String followerEmail, Long followeeId);

    List<FollowResponse> getFollowers(Long memberId);

    List<FollowResponse> getFollowees(Long memberId);

    boolean removeFollow(String followerEmail, Long followeeId);
}
