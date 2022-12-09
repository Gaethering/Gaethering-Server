package com.gaethering.gaetheringserver.member.controller;

import static com.gaethering.gaetheringserver.member.exception.errorcode.MemberErrorCode.MEMBER_NOT_FOUND;
import static com.gaethering.gaetheringserver.member.util.ApiDocumentUtils.getDocumentRequest;
import static com.gaethering.gaetheringserver.member.util.ApiDocumentUtils.getDocumentResponse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gaethering.gaetheringserver.member.dto.auth.LoginInfoResponse;
import com.gaethering.gaetheringserver.member.dto.profile.ModifyMemberNicknameRequest;
import com.gaethering.gaetheringserver.member.dto.profile.OtherProfileResponse;
import com.gaethering.gaetheringserver.member.dto.profile.OwnProfileResponse;
import com.gaethering.gaetheringserver.member.dto.profile.OtherProfileResponse.ProfilePetResponse;
import com.gaethering.gaetheringserver.member.dto.signup.SignUpRequest;
import com.gaethering.gaetheringserver.member.dto.signup.SignUpResponse;
import com.gaethering.gaetheringserver.member.exception.member.MemberNotFoundException;
import com.gaethering.gaetheringserver.member.service.member.MemberProfileService;
import com.gaethering.gaetheringserver.member.service.member.MemberService;
import com.gaethering.gaetheringserver.core.type.Gender;
import java.security.Principal;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockPart;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartHttpServletRequest;


@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@AutoConfigureRestDocs
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
    @DisplayName("닉네임 수정 - 회원 못 찾았을 때")
    public void modifyMemberNicknameMemberNotFoundFailure() throws Exception {
        //given
        ModifyMemberNicknameRequest request = new ModifyMemberNicknameRequest(
            "modifiedNickname");
        given(memberService.modifyNickname(anyString(), anyString()))
            .willThrow(new MemberNotFoundException());

        //when
        //then
        mockMvc.perform(patch("/api/mypage/nickname")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "accessToken")
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().is4xxClientError())
            .andExpect(jsonPath("$.code").value(MEMBER_NOT_FOUND.getCode()))
            .andExpect(jsonPath("$.message").value(MEMBER_NOT_FOUND.getMessage()))

            .andDo(print())
            .andDo(document("mypage/modify-nickname/failure/member-not-found",
                getDocumentRequest(),
                getDocumentResponse(),
                requestHeaders(
                    headerWithName("Authorization").description("Access Token"))
            ));
    }

    @Test
    @WithMockUser
    public void modifyMemberNicknameSuccess() throws Exception {
        //given
        ModifyMemberNicknameRequest request = new ModifyMemberNicknameRequest(
            "modifiedNickname");

        //when
        //then
        mockMvc.perform(patch("/api/mypage/nickname")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "accessToken")
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.nickname").value(request.getNickname()))

            .andDo(print())
            .andDo(document("mypage/modify-nickname/success",
                getDocumentRequest(),
                getDocumentResponse(),
                requestHeaders(
                    headerWithName("Authorization").description("Access Token"))
            ));
    }

    @Test
    @WithMockUser
    @DisplayName("회원(본인) 프로필 조회 - 회원 못 찾았을 때")
    public void getOwnProfileMemberNotFoundFailure() throws Exception {
        //given
        given(memberProfileService.getOwnProfile(anyString()))
            .willThrow(new MemberNotFoundException());

        //when
        //then
        mockMvc.perform(get("/api/mypage")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "accessToken"))
            .andExpect(status().is4xxClientError())
            .andExpect(jsonPath("$.code").value(MEMBER_NOT_FOUND.getCode()))
            .andExpect(jsonPath("$.message").value(MEMBER_NOT_FOUND.getMessage()))

            .andDo(print())
            .andDo(document("mypage/get-profile/failure/member-not-found",
                getDocumentRequest(),
                getDocumentResponse(),
                requestHeaders(
                    headerWithName("Authorization").description("Access Token"))
            ));
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
        mockMvc.perform(get("/api/mypage")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "accessToken"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.email").value(ownProfileResponse.getEmail()))
            .andExpect(jsonPath("$.nickname").value(ownProfileResponse.getNickname()))
            .andExpect(jsonPath("$.phoneNumber").value(ownProfileResponse.getPhoneNumber()))
            .andExpect(jsonPath("$.gender").value(ownProfileResponse.getGender().toString()))
            .andExpect(jsonPath("$.mannerDegree").value(
                String.valueOf(ownProfileResponse.getMannerDegree())))
            .andExpect(
                jsonPath("$.followerCount").value(
                    String.valueOf(ownProfileResponse.getFollowerCount())))
            .andExpect(
                jsonPath("$.followerCount").value(
                    String.valueOf(ownProfileResponse.getFollowingCount())))
            .andExpect(
                jsonPath("$.petCount").value(String.valueOf(ownProfileResponse.getPetCount())))
            .andExpect(jsonPath("$.pets[0].id").value(String.valueOf(petResponse.getId())))
            .andExpect(jsonPath("$.pets[0].name").value(petResponse.getName()))
            .andExpect(
                jsonPath("$.pets[0].representative").value(
                    String.valueOf(petResponse.isRepresentative())))

            .andDo(print())
            .andDo(document("mypage/get-profile/success",
                getDocumentRequest(),
                getDocumentResponse(),
                requestHeaders(
                    headerWithName("Authorization").description("Access Token"))
            ));

    }

    @Test
    @WithMockUser
    @DisplayName("회원(타인) 프로필 조회 - 회원 못 찾았을 때")
    public void getOtherProfileMemberNotFoundFailure() throws Exception {
        //given
        given(memberProfileService.getOtherProfile(anyLong()))
            .willThrow(new MemberNotFoundException());

        //when
        //then
        mockMvc.perform(get("/api/members/{memberId}/profile", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "accessToken"))
            .andExpect(status().is4xxClientError())
            .andExpect(jsonPath("$.code").value(MEMBER_NOT_FOUND.getCode()))
            .andExpect(jsonPath("$.message").value(MEMBER_NOT_FOUND.getMessage()))

            .andDo(print())
            .andDo(document("member/get-profile/failure/member-not-found",
                getDocumentRequest(),
                getDocumentResponse(),
                pathParameters(parameterWithName("memberId").description("조회할 회원 Id")),
                requestHeaders(
                    headerWithName("Authorization").description("Access Token"))
            ));
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
        mockMvc.perform(get("/api/members/{memberId}/profile", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "accessToken"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.email").value(otherProfile.getEmail()))
            .andExpect(jsonPath("$.nickname").value(otherProfile.getNickname()))
            .andExpect(jsonPath("$.gender").value(otherProfile.getGender().toString()))
            .andExpect(
                jsonPath("$.mannerDegree").value(String.valueOf(otherProfile.getMannerDegree())))
            .andExpect(
                jsonPath("$.followerCount").value(String.valueOf(otherProfile.getFollowerCount())))
            .andExpect(
                jsonPath("$.followerCount").value(String.valueOf(otherProfile.getFollowingCount())))
            .andExpect(jsonPath("$.petCount").value(String.valueOf(otherProfile.getPetCount())))
            .andExpect(jsonPath("$.pets[0].id").value(String.valueOf(petResponse.getId())))
            .andExpect(jsonPath("$.pets[0].name").value(petResponse.getName()))
            .andExpect(
                jsonPath("$.pets[0].representative").value(
                    String.valueOf(petResponse.isRepresentative())))

            .andDo(print())
            .andDo(document("member/get-profile/success",
                getDocumentRequest(),
                getDocumentResponse(),
                pathParameters(parameterWithName("memberId").description("조회할 회원 Id")),
                requestHeaders(
                    headerWithName("Authorization").description("Access Token"))
            ));
    }

    @Test
    @DisplayName("로그인 시 사용자 정보 제공")
    @WithMockUser
    public void getLoginInfo() throws Exception {
        //given
        LoginInfoResponse response = LoginInfoResponse.builder()
            .nickname("내캉")
            .petName("하울")
            .imageUrl("http://test.com")
            .build();

        given(memberService.getLoginInfo(anyString())).willReturn(response);

        //when
        //then
        mockMvc.perform(
                get("/api/members/info").contentType(MediaType.APPLICATION_JSON).with(csrf()))
            .andDo(print()).andExpect(status().isOk())
            .andExpect(jsonPath("$.nickname").value(response.getNickname()))
            .andExpect(jsonPath("$.imageUrl").value(response.getImageUrl()))
            .andExpect(jsonPath("$.petName").value(response.getPetName()));
    }

}