package com.gaethering.gaetheringserver.member.service;

public interface FollowService {

    boolean createFollow(String followerEmail, Long followeeId);
}
