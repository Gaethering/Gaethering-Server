package com.gaethering.gaetheringserver.member.controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.gaethering.gaetheringserver.member.dto.OwnProfileResponse;
import com.gaethering.gaetheringserver.member.dto.ProfilePetResponse;
import com.gaethering.gaetheringserver.member.service.MemberProfileService;
import com.gaethering.gaetheringserver.member.service.MemberService;
import com.gaethering.gaetheringserver.member.type.Gender;
import java.security.Principal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(MemberController.class)
@ActiveProfiles("test")
class MemberControllerTest {

    @MockBean
    private MemberService memberService;
    @MockBean
    private MemberProfileService memberProfileService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser
    public void getOwnProfile() throws Exception {
        //given
        String email = "test@test.com";
        ProfilePetResponse petResponse = ProfilePetResponse.builder()
            .id(1L)
            .name("pet")
            .isRepresentative(true)
            .build();
        OwnProfileResponse ownProfileResponse = OwnProfileResponse.builder()
            .email(email)
            .nickname("nickname")
            .phoneNumber("010-0000-0000")
            .gender(Gender.MALE)
            .mannerDegree(36.5f)
            .followerCount(10L)
            .followingCount(10L)
            .petCount(5)
            .pets(List.of(petResponse))
            .build();
        Principal principal = Mockito.mock(Principal.class);
        given(principal.getName()).willReturn(email);
        given(memberProfileService.getOwnProfile(anyString()))
            .willReturn(ownProfileResponse);

        //when
        //then
        mockMvc.perform(get("/api/mypage")
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf()))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.email").value(ownProfileResponse.getEmail()))
            .andExpect(jsonPath("$.nickname").value(ownProfileResponse.getNickname()))
            .andExpect(jsonPath("$.phoneNumber").value(ownProfileResponse.getPhoneNumber()))
            .andExpect(jsonPath("$.gender").value(ownProfileResponse.getGender().toString()))
            .andExpect(jsonPath("$.mannerDegree").value(
                String.valueOf(ownProfileResponse.getMannerDegree())))
            .andExpect(jsonPath("$.followerCount").value(
                String.valueOf(ownProfileResponse.getFollowerCount())))
            .andExpect(jsonPath("$.followerCount").value(
                String.valueOf(ownProfileResponse.getFollowingCount())))
            .andExpect(
                jsonPath("$.petCount").value(String.valueOf(ownProfileResponse.getPetCount())))
            .andExpect(jsonPath("$.pets[0].id").value(String.valueOf(petResponse.getId())))
            .andExpect(jsonPath("$.pets[0].name").value(petResponse.getName()))
            .andExpect(jsonPath("$.pets[0].representative").value(
                String.valueOf(petResponse.isRepresentative())));
    }
}