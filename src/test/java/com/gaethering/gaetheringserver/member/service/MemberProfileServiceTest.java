package com.gaethering.gaetheringserver.member.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

import com.gaethering.gaetheringserver.member.domain.Member;
import com.gaethering.gaetheringserver.member.domain.MemberProfile;
import com.gaethering.gaetheringserver.member.dto.profile.OtherProfileResponse;
import com.gaethering.gaetheringserver.member.dto.profile.OwnProfileResponse;
import com.gaethering.gaetheringserver.member.exception.member.MemberNotFoundException;
import com.gaethering.gaetheringserver.member.exception.errorcode.MemberErrorCode;
import com.gaethering.gaetheringserver.member.repository.follow.FollowRepository;
import com.gaethering.gaetheringserver.member.repository.member.MemberRepository;
import com.gaethering.gaetheringserver.member.service.member.MemberProfileServiceImpl;
import com.gaethering.gaetheringserver.core.type.Gender;
import com.gaethering.gaetheringserver.pet.domain.Pet;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MemberProfileServiceTest {

    @Mock
    private MemberRepository memberRepository;
    @Mock
    private FollowRepository followRepository;
    @InjectMocks
    private MemberProfileServiceImpl memberProfileService;

    private Member member;

    @BeforeEach
    public void setUP() {
        Pet pet1 = Pet.builder()
            .id(1L)
            .name("pet1")
            .isRepresentative(true)
            .build();
        Pet pet2 = Pet.builder()
            .id(2L)
            .name("pet2")
            .isRepresentative(false)
            .build();
        List<Pet> pets = List.of(pet1, pet2);
        MemberProfile memberProfile = MemberProfile.builder()
            .id(1L)
            .gender(Gender.MALE)
            .phoneNumber("010-0000-0000")
            .mannerDegree(36.5f)
            .build();
        member = Member.builder()
            .id(1L)
            .email("member1@test.com")
            .memberProfile(memberProfile)
            .pets(pets)
            .build();
    }

    @Test
    public void getOwnProfileFailure() {
        //given
        given(memberRepository.findByEmail(anyString()))
            .willReturn(Optional.empty());

        //when
        //then
        MemberNotFoundException exception = assertThrows(
            MemberNotFoundException.class, () -> memberProfileService.getOwnProfile(anyString()));
        assertThat(exception.getErrorCode()).isEqualTo(MemberErrorCode.MEMBER_NOT_FOUND);
    }

    @Test
    public void getOwnProfileSuccess() {
        //given
        Long followerCount = 3L;
        Long followingCount = 30L;
        given(memberRepository.findByEmail(anyString()))
            .willReturn(Optional.of(member));
        given(followRepository.countByFollowee(any(member.getClass())))
            .willReturn(followerCount);
        given(followRepository.countByFollower(any(member.getClass())))
            .willReturn(followingCount);

        //when
        OwnProfileResponse profile = memberProfileService.getOwnProfile(anyString());

        //then
        assertOwnProfile(profile, followerCount, followingCount);
    }

    @Test
    public void getOtherProfileFailure() {
        //given
        given(memberRepository.findById(anyLong()))
            .willReturn(Optional.empty());

        //when
        //then
        MemberNotFoundException exception = assertThrows(MemberNotFoundException.class,
            () -> memberProfileService.getOtherProfile(anyLong()));
        assertThat(exception.getErrorCode()).isEqualTo(MemberErrorCode.MEMBER_NOT_FOUND);
    }

    @Test
    public void getOtherProfileSuccess() {
        //given
        Long followerCount = 3L;
        Long followingCount = 30L;
        given(memberRepository.findById(anyLong()))
            .willReturn(Optional.of(member));
        given(followRepository.countByFollowee(any(member.getClass())))
            .willReturn(followerCount);
        given(followRepository.countByFollower(any(member.getClass())))
            .willReturn(followingCount);

        //when
        OtherProfileResponse profile = memberProfileService.getOtherProfile(anyLong());

        //then
        assertOtherProfile(profile, followerCount, followingCount);
    }

    private void assertOwnProfile(OwnProfileResponse profile, Long followerCount,
        Long followingCount) {
        assertThat(profile.getEmail()).isEqualTo(member.getEmail());
        assertThat(profile.getNickname()).isEqualTo(member.getNickname());
        assertThat(profile.getPhoneNumber()).isEqualTo(member.getMemberProfile().getPhoneNumber());
        assertThat(profile.getGender()).isEqualTo(member.getMemberProfile().getGender());
        assertThat(profile.getMannerDegree()).isEqualTo(
            member.getMemberProfile().getMannerDegree());
        assertThat(profile.getPetCount()).isEqualTo(member.getPets().size());
        assertThat(profile.getPets().get(0).getId()).isEqualTo(member.getPets().get(0).getId());
        assertThat(profile.getPets().get(1).getId()).isEqualTo(member.getPets().get(1).getId());
        assertThat(profile.getFollowerCount()).isEqualTo(followerCount);
        assertThat(profile.getFollowingCount()).isEqualTo(followingCount);
    }

    private void assertOtherProfile(OtherProfileResponse profile, Long followerCount,
        Long followingCount) {
        assertThat(profile.getEmail()).isEqualTo(member.getEmail());
        assertThat(profile.getNickname()).isEqualTo(member.getNickname());
        assertThat(profile.getGender()).isEqualTo(member.getMemberProfile().getGender());
        assertThat(profile.getMannerDegree()).isEqualTo(
            member.getMemberProfile().getMannerDegree());
        assertThat(profile.getPetCount()).isEqualTo(member.getPets().size());
        assertThat(profile.getPets().get(0).getId()).isEqualTo(member.getPets().get(0).getId());
        assertThat(profile.getPets().get(1).getId()).isEqualTo(member.getPets().get(1).getId());
        assertThat(profile.getFollowerCount()).isEqualTo(followerCount);
        assertThat(profile.getFollowingCount()).isEqualTo(followingCount);
    }

}