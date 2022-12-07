package com.gaethering.gaetheringserver.member.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gaethering.gaetheringserver.config.SecurityConfig;
import com.gaethering.gaetheringserver.filter.JwtAuthenticationFilter;
import com.gaethering.gaetheringserver.member.dto.OtherProfileResponse;
import com.gaethering.gaetheringserver.member.dto.OwnProfileResponse;
import com.gaethering.gaetheringserver.member.dto.ProfilePetResponse;
import com.gaethering.gaetheringserver.member.dto.SignUpRequest;
import com.gaethering.gaetheringserver.member.dto.SignUpResponse;
import com.gaethering.gaetheringserver.member.service.MemberProfileService;
import com.gaethering.gaetheringserver.member.service.MemberService;
import com.gaethering.gaetheringserver.member.type.Gender;
import java.security.Principal;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockPart;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartHttpServletRequest;


@WebMvcTest(controllers = MemberController.class, excludeFilters = {
    @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
        classes = {SecurityConfig.class, JwtAuthenticationFilter.class})
})
@ActiveProfiles("test")
class MemberControllerTest {

    @MockBean
    private MemberService memberService;

    @MockBean
    private MemberProfileService memberProfileService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("회원 가입 시 파일과 json 데이터 전송")
    @WithMockUser
    void signUp() throws Exception {
        //given
        SignUpRequest request = SignUpRequest.builder()
            .email("gaethering@gmail.com")
            .nickname("개더링")
            .password("1234qwer!")
            .passwordCheck("1234qwer!")
            .name("김진호")
            .phone("010-1230-1234")
            .birth("2017-03-15")
            .gender("MALE")
            .isEmailAuth(true)
            .petName("뽀삐")
            .petBirth("2022-03-15")
            .weight(5.5f)
            .breed("말티즈")
            .petGender("FEMALE")
            .description("___")
            .isNeutered(true)
            .build();

        String filename = "test.png";
        String contentType = "image/png";

        MockMultipartFile image = new MockMultipartFile("image", filename, contentType,
            "test".getBytes());

        String requestString = objectMapper.writeValueAsString(request);

        MockPart data = new MockPart("data", requestString.getBytes());
        data.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        given(memberService.signUp(image, request))
            .willReturn(SignUpResponse.builder()
                .imageUrl(image.getName())
                .petName(request.getPetName())
                .build());

        //when

        //then
        MultipartHttpServletRequest mockRequest = (MultipartHttpServletRequest) mockMvc.perform(
                multipart("/api/members/sign-up")
                    .file(image)
                    .part(data)
                    .with(csrf())
            ).andDo(print())
            .andExpect(status().isCreated())
            .andReturn().getRequest();

        assertEquals(1, mockRequest.getParts().size());

        assertEquals(filename,
            mockRequest.getMultiFileMap().get("image").get(0).getOriginalFilename());
    }

    @Test
    @WithMockUser
    public void modifyMemberNickname() throws Exception {
        //given
        String email = "test@test.com";
        String nickname = "modifiedNickname";
        Principal principal = Mockito.mock(Principal.class);
        given(principal.getName()).willReturn(email);

        //when
        //then
        mockMvc.perform(patch("/api/mypage/nickname")
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .content(nickname))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.nickname").value(nickname));
    }

    @Test
    @WithMockUser
    public void getOwnProfile() throws Exception {
        //given
        ProfilePetResponse petResponse = ProfilePetResponse.builder().id(1L).name("pet")
            .isRepresentative(true).build();
        OwnProfileResponse ownProfileResponse = OwnProfileResponse.builder().email("test@test.com")
            .nickname("nickname").phoneNumber("010-0000-0000").gender(Gender.MALE)
            .mannerDegree(36.5f).followerCount(10L).followingCount(10L).petCount(5)
            .pets(List.of(petResponse)).build();
        Principal principal = Mockito.mock(Principal.class);
        given(principal.getName()).willReturn(ownProfileResponse.getEmail());
        given(memberProfileService.getOwnProfile(anyString())).willReturn(ownProfileResponse);

        //when
        //then
        mockMvc.perform(get("/api/mypage").contentType(MediaType.APPLICATION_JSON).with(csrf()))
            .andDo(print()).andExpect(status().isOk())
            .andExpect(jsonPath("$.email").value(ownProfileResponse.getEmail()))
            .andExpect(jsonPath("$.nickname").value(ownProfileResponse.getNickname()))
            .andExpect(jsonPath("$.phoneNumber").value(ownProfileResponse.getPhoneNumber()))
            .andExpect(jsonPath("$.gender").value(ownProfileResponse.getGender().toString()))
            .andExpect(jsonPath("$.mannerDegree").value(
                String.valueOf(ownProfileResponse.getMannerDegree()))).andExpect(
                jsonPath("$.followerCount").value(
                    String.valueOf(ownProfileResponse.getFollowerCount()))).andExpect(
                jsonPath("$.followerCount").value(
                    String.valueOf(ownProfileResponse.getFollowingCount()))).andExpect(
                jsonPath("$.petCount").value(String.valueOf(ownProfileResponse.getPetCount())))
            .andExpect(jsonPath("$.pets[0].id").value(String.valueOf(petResponse.getId())))
            .andExpect(jsonPath("$.pets[0].name").value(petResponse.getName())).andExpect(
                jsonPath("$.pets[0].representative").value(
                    String.valueOf(petResponse.isRepresentative())));
    }

    @Test
    @WithMockUser
    public void getOtherProfile() throws Exception {
        //given
        ProfilePetResponse petResponse = ProfilePetResponse.builder().id(1L).name("pet")
            .isRepresentative(true).build();
        OtherProfileResponse otherProfile = OtherProfileResponse.builder().email("test@test.com")
            .nickname("nickname").gender(Gender.MALE).mannerDegree(36.5f).followerCount(10L)
            .followingCount(10L).petCount(5).pets(List.of(petResponse)).build();
        given(memberProfileService.getOtherProfile(anyLong())).willReturn(otherProfile);

        //when
        //then
        mockMvc.perform(
                get("/api/members/1/profile").contentType(MediaType.APPLICATION_JSON).with(csrf()))
            .andDo(print()).andExpect(status().isOk())
            .andExpect(jsonPath("$.email").value(otherProfile.getEmail()))
            .andExpect(jsonPath("$.nickname").value(otherProfile.getNickname()))
            .andExpect(jsonPath("$.gender").value(otherProfile.getGender().toString())).andExpect(
                jsonPath("$.mannerDegree").value(String.valueOf(otherProfile.getMannerDegree())))
            .andExpect(
                jsonPath("$.followerCount").value(String.valueOf(otherProfile.getFollowerCount())))
            .andExpect(
                jsonPath("$.followerCount").value(String.valueOf(otherProfile.getFollowingCount())))
            .andExpect(jsonPath("$.petCount").value(String.valueOf(otherProfile.getPetCount())))
            .andExpect(jsonPath("$.pets[0].id").value(String.valueOf(petResponse.getId())))
            .andExpect(jsonPath("$.pets[0].name").value(petResponse.getName())).andExpect(
                jsonPath("$.pets[0].representative").value(
                    String.valueOf(petResponse.isRepresentative())));
    }

}