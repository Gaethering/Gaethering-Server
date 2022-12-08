package com.gaethering.gaetheringserver.member.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

import com.gaethering.gaetheringserver.member.domain.Follow;
import com.gaethering.gaetheringserver.member.domain.Member;
import com.gaethering.gaetheringserver.member.dto.FollowResponse;
import com.gaethering.gaetheringserver.member.exception.MemberNotFoundException;
import com.gaethering.gaetheringserver.member.repository.follow.FollowRepository;
import com.gaethering.gaetheringserver.member.repository.member.MemberRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FollowServiceTest {

    @Mock
    private FollowRepository followRepository;
    @Mock
    private MemberRepository memberRepository;
    @InjectMocks
    private FollowServiceImpl followService;
    private List<Member> members;

    @BeforeEach
    void setUp() {
        members = new ArrayList<>();
        members.add(Member.builder().id(1L).email("member1@test.com").build());
        members.add(Member.builder().id(2L).email("member2@test.com").build());
    }

    @Test
    @DisplayName("이메일로 회원 못 찾았을 때")
    public void createFollowMemberNotFoundByEmailFailure() {
        //given
        given(memberRepository.findByEmail(anyString()))
            .willReturn(Optional.empty());

        //when
        //then
        assertThrows(MemberNotFoundException.class,
            () -> followService.createFollow("test@test.com", 1L));
    }

    @Test
    @DisplayName("아이디로 회원 못 찾았을 때")
    public void createFollowMemberNotFoundByIdFailure() {
        //given
        Member member = members.get(0);
        given(memberRepository.findByEmail(anyString()))
            .willReturn(Optional.of(member));
        given(memberRepository.findById(anyLong()))
            .willReturn(Optional.empty());

        //when
        //then
        assertThrows(MemberNotFoundException.class,
            () -> followService.createFollow("test@test.com", 1L));
    }

    @Test
    public void createFollowSuccess() {
        //given
        Member follower = members.get(0);
        Member followee = members.get(1);
        given(memberRepository.findByEmail(anyString()))
            .willReturn(Optional.of(follower));
        given(memberRepository.findById(anyLong()))
            .willReturn(Optional.of(followee));

        //when
        boolean result = followService.createFollow(follower.getEmail(), followee.getId());

        //then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("회원 못 찾았을 때")
    public void getFollowerMemberNotFoundFailure() {
        //given
        given(memberRepository.findById(anyLong()))
            .willReturn(Optional.empty());

        //when
        //then
        assertThrows(MemberNotFoundException.class,
            () -> followService.getFollowers(anyLong()));
    }

    @Test
    @DisplayName("팔로워 0명 일떄")
    public void getFollowerWhenEmpty() {
        //given
        Member member = members.get(0);
        given(memberRepository.findById(anyLong()))
            .willReturn(Optional.of(member));
        given(followRepository.findByFollowee(any()))
            .willReturn(Collections.emptyList());

        //when
        List<FollowResponse> follower = followService.getFollowers(member.getId());

        //then
        assertThat(follower.isEmpty()).isTrue();
    }

    @Test
    public void getFollowerSuccess() {
        //given
        Member followee = members.get(0);
        Member follower = members.get(1);
        Follow follow = Follow.builder()
            .followee(followee)
            .follower(follower).build();
        given(memberRepository.findById(anyLong()))
            .willReturn(Optional.of(followee));
        given(followRepository.findByFollowee(followee))
            .willReturn(List.of(follow));

        //when
        List<FollowResponse> followResponse = followService.getFollowers(followee.getId());

        //then
        assertThat(followResponse.size()).isEqualTo(1);
        assertThat(followResponse.get(0).getId()).isEqualTo(follower.getId());
        assertThat(followResponse.get(0).getName()).isEqualTo(follower.getName());
        assertThat(followResponse.get(0).getNickname()).isEqualTo(follower.getNickname());
    }

    @Test
    @DisplayName("회원 못 찾았을 때")
    public void getFolloweeMemberNotFoundFailure() {
        //given
        given(memberRepository.findById(anyLong()))
            .willReturn(Optional.empty());

        //when
        //then
        assertThrows(MemberNotFoundException.class,
            () -> followService.getFollowees(anyLong()));
    }

    @Test
    @DisplayName("팔로워 0명 일떄")
    public void getFolloweeWhenEmpty() {
        //given
        Member member = members.get(0);
        given(memberRepository.findById(anyLong()))
            .willReturn(Optional.of(member));
        given(followRepository.findByFollower(any()))
            .willReturn(Collections.emptyList());

        //when
        List<FollowResponse> follower = followService.getFollowees(member.getId());

        //then
        assertThat(follower.isEmpty()).isTrue();
    }

    @Test
    public void getFolloweeSuccess() {
        //given
        Member follower = members.get(0);
        Member followee = members.get(1);
        Follow follow = Follow.builder()
            .followee(followee)
            .follower(follower).build();
        given(memberRepository.findById(anyLong()))
            .willReturn(Optional.of(follower));
        given(followRepository.findByFollower(follower))
            .willReturn(List.of(follow));

        //when
        List<FollowResponse> followResponse = followService.getFollowees(follower.getId());

        //then
        assertThat(followResponse.size()).isEqualTo(1);
        assertThat(followResponse.get(0).getId()).isEqualTo(followee.getId());
        assertThat(followResponse.get(0).getName()).isEqualTo(followee.getName());
        assertThat(followResponse.get(0).getNickname()).isEqualTo(followee.getNickname());
    }

    @Test
    @DisplayName("이메일로 회원 못 찾았을 때")
    public void removeFollowMemberNotFoundByEmailFailure() {
        //given
        given(memberRepository.findByEmail(anyString()))
            .willReturn(Optional.empty());

        //when
        //then
        assertThrows(MemberNotFoundException.class,
            () -> followService.removeFollow("test@test.com", 1L));
    }

    @Test
    @DisplayName("아이디로 회원 못 찾았을 때")
    public void removeFollowMemberNotFoundByIdFailure() {
        //given
        Member member = members.get(0);
        given(memberRepository.findByEmail(anyString()))
            .willReturn(Optional.of(member));
        given(memberRepository.findById(anyLong()))
            .willReturn(Optional.empty());

        //when
        //then
        assertThrows(MemberNotFoundException.class,
            () -> followService.removeFollow("test@test.com", 1L));
    }

    @Test
    @DisplayName("해당하는 팔로우가 존재하지 않을 때")
    public void removeFollowFollowNotFoundFailure() {
        //given
        Member follower = members.get(0);
        Member followee = members.get(1);
        given(memberRepository.findByEmail(anyString()))
            .willReturn(Optional.of(follower));
        given(memberRepository.findById(anyLong()))
            .willReturn(Optional.of(followee));
        given(followRepository.removeByFollowerAndFollowee(follower, followee))
            .willReturn(0);

        //when
        boolean result = followService.removeFollow(follower.getEmail(), followee.getId());

        //then
        assertThat(result).isFalse();
    }

    @Test
    public void removeFollowSuccess() {
        //given
        Member follower = members.get(0);
        Member followee = members.get(1);
        given(memberRepository.findByEmail(anyString()))
            .willReturn(Optional.of(follower));
        given(memberRepository.findById(anyLong()))
            .willReturn(Optional.of(followee));
        given(followRepository.removeByFollowerAndFollowee(follower, followee))
            .willReturn(1);

        //when
        boolean result = followService.removeFollow(follower.getEmail(), followee.getId());

        //then
        assertThat(result).isTrue();
    }
}