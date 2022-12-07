package com.gaethering.gaetheringserver.member.service;

import com.gaethering.gaetheringserver.member.domain.Follow;
import com.gaethering.gaetheringserver.member.domain.Member;
import com.gaethering.gaetheringserver.member.exception.MemberNotFoundException;
import com.gaethering.gaetheringserver.member.repository.follow.FollowRepository;
import com.gaethering.gaetheringserver.member.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FollowServiceImpl implements FollowService {

    private final FollowRepository followRepository;
    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public boolean createFollow(String followerEmail, Long followeeId) {
        Member follower = memberRepository.findByEmail(followerEmail)
            .orElseThrow(MemberNotFoundException::new);
        Member followee = memberRepository.findById(followeeId)
            .orElseThrow(MemberNotFoundException::new);
        followRepository.save(Follow.builder().follower(follower).followee(followee).build());
        return true;
    }
}
