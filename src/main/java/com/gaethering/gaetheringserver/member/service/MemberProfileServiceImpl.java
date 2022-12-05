package com.gaethering.gaetheringserver.member.service;

import com.gaethering.gaetheringserver.member.domain.Member;
import com.gaethering.gaetheringserver.member.dto.OtherProfileResponse;
import com.gaethering.gaetheringserver.member.dto.OwnProfileResponse;
import com.gaethering.gaetheringserver.member.exception.member.MemberNotFoundException;
import com.gaethering.gaetheringserver.member.repository.follow.FollowRepository;
import com.gaethering.gaetheringserver.member.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberProfileServiceImpl implements MemberProfileService {

    private final MemberRepository memberRepository;
    private final FollowRepository followRepository;

    @Override
    public OwnProfileResponse getOwnProfile(String email) {
        Member member = memberRepository.findByEmail(email)
            .orElseThrow(MemberNotFoundException::new);
        Long followerCount = followRepository.countByFollowee(member);
        Long followingCount = followRepository.countByFollower(member);
        return OwnProfileResponse.of(member, followerCount, followingCount);
    }

    @Override
    public OtherProfileResponse getOtherProfile(Long memberId) {
        Member member = memberRepository.findById(memberId)
            .orElseThrow(MemberNotFoundException::new);
        Long followerCount = followRepository.countByFollowee(member);
        Long followingCount = followRepository.countByFollower(member);
        return OtherProfileResponse.of(member, followerCount, followingCount);
    }


}
