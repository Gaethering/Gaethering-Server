package com.gaethering.gaetheringserver.member.service.follow;

import com.gaethering.gaetheringserver.member.domain.Follow;
import com.gaethering.gaetheringserver.member.domain.Member;
import com.gaethering.gaetheringserver.member.dto.follow.FollowResponse;
import com.gaethering.gaetheringserver.member.exception.member.MemberNotFoundException;
import com.gaethering.gaetheringserver.member.repository.follow.FollowRepository;
import com.gaethering.gaetheringserver.member.repository.member.MemberRepository;
import java.util.List;
import java.util.stream.Collectors;
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
        Member follower = getMemberByEmail(followerEmail);
        Member followee = getMemberById(followeeId);
        followRepository.save(Follow.builder().follower(follower).followee(followee).build());
        return true;
    }

    @Override
    public List<FollowResponse> getFollowers(Long memberId) {
        List<Follow> follows = followRepository.findByFollowee(getMemberById(memberId));
        return follows.stream()
            .map(follow -> FollowResponse.of(follow.getFollower())).collect(Collectors.toList());
    }

    @Override
    public List<FollowResponse> getFollowees(Long memberId) {
        List<Follow> follows = followRepository.findByFollower(getMemberById(memberId));
        return follows.stream()
            .map(follow -> FollowResponse.of(follow.getFollowee())).collect(Collectors.toList());
    }

    @Override
    public boolean removeFollow(String followerEmail, Long followeeId) {
        Member follower = getMemberByEmail(followerEmail);
        Member followee = getMemberById(followeeId);
        Integer count = followRepository.removeByFollowerAndFollowee(follower, followee);
        return count >= 1;
    }

    private Member getMemberByEmail(String email) {
        return memberRepository.findByEmail(email)
            .orElseThrow(MemberNotFoundException::new);
    }

    private Member getMemberById(Long id) {
        return memberRepository.findById(id)
            .orElseThrow(MemberNotFoundException::new);
    }
}
