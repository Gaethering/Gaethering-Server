package com.gaethering.gaetheringserver.member.controller;

import static com.gaethering.gaetheringserver.domain.member.exception.errorcode.MemberErrorCode.DUPLICATED_EMAIL;
import static com.gaethering.gaetheringserver.domain.member.exception.errorcode.MemberErrorCode.INCORRECT_REFRESH_TOKEN;
import static com.gaethering.gaetheringserver.domain.member.exception.errorcode.MemberErrorCode.INVALID_REFRESH_TOKEN;
import static com.gaethering.gaetheringserver.domain.member.exception.errorcode.MemberErrorCode.MEMBER_NOT_FOUND;
import static com.gaethering.gaetheringserver.domain.member.exception.errorcode.MemberErrorCode.NOT_EXIST_REFRESH_TOKEN;
import static com.gaethering.gaetheringserver.member.util.ApiDocumentUtils.getDocumentRequest;
import static com.gaethering.gaetheringserver.member.util.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestPartFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.relaxedRequestParts;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gaethering.gaetheringserver.core.type.Gender;
import com.gaethering.gaetheringserver.domain.member.dto.auth.LoginInfoResponse;
import com.gaethering.gaetheringserver.domain.member.dto.auth.LoginRequest;
import com.gaethering.gaetheringserver.domain.member.dto.auth.LoginResponse;
import com.gaethering.gaetheringserver.domain.member.dto.auth.ReissueTokenRequest;
import com.gaethering.gaetheringserver.domain.member.dto.auth.ReissueTokenResponse;
import com.gaethering.gaetheringserver.domain.member.dto.mypage.MyPostsResponse;
import com.gaethering.gaetheringserver.domain.member.dto.mypage.PostInfo;
import com.gaethering.gaetheringserver.domain.member.dto.profile.ModifyMemberNicknameRequest;
import com.gaethering.gaetheringserver.domain.member.dto.profile.OtherProfileResponse;
import com.gaethering.gaetheringserver.domain.member.dto.profile.OwnProfileResponse;
import com.gaethering.gaetheringserver.domain.member.dto.profile.ProfilePetResponse;
import com.gaethering.gaetheringserver.domain.member.dto.signup.SignUpRequest;
import com.gaethering.gaetheringserver.domain.member.dto.signup.SignUpResponse;
import com.gaethering.gaetheringserver.domain.member.exception.auth.TokenIncorrectException;
import com.gaethering.gaetheringserver.domain.member.exception.auth.TokenInvalidException;
import com.gaethering.gaetheringserver.domain.member.exception.auth.TokenNotExistException;
import com.gaethering.gaetheringserver.domain.member.exception.member.DuplicatedEmailException;
import com.gaethering.gaetheringserver.domain.member.exception.member.MemberNotFoundException;
import com.gaethering.gaetheringserver.domain.member.service.auth.AuthService;
import com.gaethering.gaetheringserver.domain.member.service.member.MemberProfileService;
import com.gaethering.gaetheringserver.domain.member.service.member.MemberService;
import java.security.Principal;
import java.time.LocalDateTime;
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
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;


@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@AutoConfigureRestDocs
class MemberControllerTest {

    @MockBean
    private MemberService memberService;

    @MockBean
    private AuthService authService;

    @MockBean
    private MemberProfileService memberProfileService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("회원 가입 시 파일과 json 데이터 전송")
    void signUp_Success() throws Exception {
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
            .emailAuth(true)
            .petName("뽀삐")
            .petBirth("2022-03-15")
            .weight(5.5f)
            .breed("말티즈")
            .petGender("FEMALE")
            .description("___")
            .neutered(true)
            .build();

        MockMultipartFile image = new MockMultipartFile("image", "test.png",
            "image/png", "test".getBytes());

        String requestString = objectMapper.writeValueAsString(request);

        MockMultipartFile data = new MockMultipartFile("data", "",
            "application/json", requestString.getBytes());

        given(memberService.signUp(any(), any()))
            .willReturn(SignUpResponse.builder()
                .imageUrl(image.getName())
                .petName(request.getPetName())
                .build());

        //when
        //then
        mockMvc.perform(
                multipart("/api/members/sign-up")
                    .file(image)
                    .file(data)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
            )
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.petName").value(request.getPetName()))
            .andExpect(jsonPath("$.imageUrl").value(image.getName()))
            .andExpect(status().isCreated())
            .andDo(print())
            .andDo(document("member/sign-up/success",
                relaxedRequestParts(
                    partWithName("image").description("펫 프로필 사진"),
                    partWithName("data").description("회원 가입에 필요한 데이터")
                )
            ))
            .andDo(document("member/sign-up/success",
                requestPartFields("data",
                    fieldWithPath("email").type(JsonFieldType.STRING).description("사용자 이메일"),
                    fieldWithPath("nickname").type(JsonFieldType.STRING).description("사용자 닉네임"),
                    fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호"),
                    fieldWithPath("passwordCheck").type(JsonFieldType.STRING).description("비밀번호 확인"),
                    fieldWithPath("name").type(JsonFieldType.STRING).description("사용자 이름"),
                    fieldWithPath("phone").type(JsonFieldType.STRING).description("사용자 전화번호"),
                    fieldWithPath("birth").type(JsonFieldType.STRING).description("사용자 생일"),
                    fieldWithPath("gender").type(JsonFieldType.STRING).description("사용자 성별"),
                    fieldWithPath("isEmailAuth").type(JsonFieldType.BOOLEAN).description("이메일 인증 여부"),
                    fieldWithPath("petName").type(JsonFieldType.STRING).description("반려동물 이름"),
                    fieldWithPath("petBirth").type(JsonFieldType.STRING).description("반려동물 생일"),
                    fieldWithPath("breed").type(JsonFieldType.STRING).description("반려동물 견종"),
                    fieldWithPath("weight").type(JsonFieldType.NUMBER).description("반려동물 몸무게"),
                    fieldWithPath("petGender").type(JsonFieldType.STRING).description("반려동물 성별"),
                    fieldWithPath("description").type(JsonFieldType.STRING).description("반려동물 설명"),
                    fieldWithPath("isNeutered").type(JsonFieldType.BOOLEAN)
                        .description("반려동물 중성화 여부")
                )
            ))
            .andDo(document("member/sign-up/success",
                getDocumentRequest()
            ))
            .andDo(document("member/sign-up/success",
                getDocumentResponse()
            ));
    }

    @Test
    @DisplayName("회원 가입 실패 - 이미 존재하는 이메일인 경우")
    public void signUp_Failure_DuplicatedEmail() throws Exception {
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
            .emailAuth(true)
            .petName("뽀삐")
            .petBirth("2022-03-15")
            .weight(5.5f)
            .breed("말티즈")
            .petGender("FEMALE")
            .description("___")
            .neutered(true)
            .build();

        String filename = "test.png";
        String contentType = "image/png";

        MockMultipartFile image = new MockMultipartFile("image", filename, contentType,
            "test".getBytes());

        String requestString = objectMapper.writeValueAsString(request);

        MockPart data = new MockPart("data", requestString.getBytes());
        data.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        given(memberService.signUp(any(), any()))
            .willThrow(new DuplicatedEmailException());

        //when
        //then
        mockMvc.perform(multipart("/api/members/sign-up")
                .file("image", image.getBytes())
                .part(data)
                .contentType(MediaType.MULTIPART_FORM_DATA)
            )
            .andExpect(status().is4xxClientError())
            .andExpect(jsonPath("$.code").value(DUPLICATED_EMAIL.getCode()))
            .andExpect(jsonPath("$.message").value(DUPLICATED_EMAIL.getMessage()))

            .andDo(print())
            .andDo(document("member/sign-up/failure/duplicated-email",
                getDocumentRequest(),
                getDocumentResponse()
            ));
    }

    @Test
    @DisplayName("로그인 성공")
    public void login() throws Exception {

        LoginResponse response = LoginResponse.builder()
            .refreshToken("refresh-token")
            .accessToken("access-token")
            .build();

        LoginRequest request = LoginRequest.builder()
            .email("test-email")
            .password("test-password")
            .build();

        Mockito.when(authService.login(any(LoginRequest.class)))
            .thenReturn(response);

        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/members/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accessToken").value(response.getAccessToken()))
            .andExpect(jsonPath("$.refreshToken").value(response.getRefreshToken()))

            .andDo(print())
            .andDo(document("member/auth/login/success",
                getDocumentRequest(),
                getDocumentResponse()
            ));

    }

    @Test
    @WithMockUser
    @DisplayName("access token 재발급 성공")
    public void reissueAccessToken_Success() throws Exception {

        ReissueTokenResponse response = ReissueTokenResponse.builder()
            .accessToken("new-access-token")
            .build();

        ReissueTokenRequest request = ReissueTokenRequest.builder()
            .refreshToken("refresh-token")
            .build();

        Mockito.when(authService.reissue(any(ReissueTokenRequest.class)))
            .thenReturn(response);

        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/members/auth/reissue-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accessToken").value(response.getAccessToken()))
            .andDo(print())
            .andDo(document("member/auth/reissue-token/success",
                getDocumentRequest(),
                getDocumentResponse()
            ));
    }

    @Test
    @WithMockUser
    @DisplayName("access token 재발급 실패 - 유효하지 않은 refresh token")
    public void reissueAccessToken_Failure_INVALID() throws Exception {

        ReissueTokenRequest request = ReissueTokenRequest.builder()
            .refreshToken("refresh-token")
            .build();

        Mockito.when(authService.reissue(any(ReissueTokenRequest.class)))
            .thenThrow(new TokenInvalidException(INVALID_REFRESH_TOKEN));

        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/members/auth/reissue-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(csrf()))
            .andExpect(status().is4xxClientError())
            .andExpect(jsonPath("$.code").value(INVALID_REFRESH_TOKEN.getCode()))
            .andExpect(jsonPath("$.message").value(INVALID_REFRESH_TOKEN.getMessage()))
            .andDo(print())
            .andDo(document("member/auth/reissue-token/failure/invalid-refresh-token",
                getDocumentRequest(),
                getDocumentResponse()
            ));
    }

    @Test
    @WithMockUser
    @DisplayName("access token 재발급 실패 - 존재하지 않은 refresh token")
    public void reissueAccessToken_Failure_NOEXIST() throws Exception {

        ReissueTokenRequest request = ReissueTokenRequest.builder()
            .refreshToken("refresh-token")
            .build();

        Mockito.when(authService.reissue(any(ReissueTokenRequest.class)))
            .thenThrow(new TokenNotExistException(NOT_EXIST_REFRESH_TOKEN));

        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/members/auth/reissue-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(csrf()))
            .andExpect(status().is4xxClientError())
            .andExpect(jsonPath("$.code").value(NOT_EXIST_REFRESH_TOKEN.getCode()))
            .andExpect(jsonPath("$.message").value(NOT_EXIST_REFRESH_TOKEN.getMessage()))
            .andDo(print())
            .andDo(document("member/auth/reissue-token/failure/not-exist-refresh-token",
                getDocumentRequest(),
                getDocumentResponse()
            ));
    }

    @Test
    @WithMockUser
    @DisplayName("access token 재발급 실패 - 일치하지 않은 refresh token")
    public void reissueAccessToken_Failure_INCORRECT() throws Exception {

        ReissueTokenRequest request = ReissueTokenRequest.builder()
            .refreshToken("refresh-token")
            .build();

        Mockito.when(authService.reissue(any(ReissueTokenRequest.class)))
            .thenThrow(new TokenIncorrectException(INCORRECT_REFRESH_TOKEN));

        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/members/auth/reissue-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(csrf()))
            .andExpect(status().is4xxClientError())
            .andExpect(jsonPath("$.code").value(INCORRECT_REFRESH_TOKEN.getCode()))
            .andExpect(jsonPath("$.message").value(INCORRECT_REFRESH_TOKEN.getMessage()))
            .andDo(print())
            .andDo(document("member/auth/reissue-token/failure/incorrect-refresh-token",
                getDocumentRequest(),
                getDocumentResponse()
            ));
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
        ProfilePetResponse petResponse = ProfilePetResponse.builder().id(1L).name("pet").isRepresentative(true)
            .imageUrl("imageUrl").build();
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
        ProfilePetResponse petResponse = ProfilePetResponse.builder().id(1L).name("pet").isRepresentative(true)
            .imageUrl("imageUrl").build();
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
            .memberId(1L)
            .nickname("내캉")
            .petName("하울")
            .imageUrl("http://test.com")
            .build();
        given(memberService.getLoginInfo(anyString())).willReturn(response);

        //when
        //then
        mockMvc.perform(
                get("/api/members/info")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "accessToken"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.memberId").value(String.valueOf(response.getMemberId())))
            .andExpect(jsonPath("$.nickname").value(response.getNickname()))
            .andExpect(jsonPath("$.imageUrl").value(response.getImageUrl()))
            .andExpect(jsonPath("$.petName").value(response.getPetName()))

            .andDo(print())
            .andDo(document("member/get-longin-info/success",
                getDocumentRequest(),
                getDocumentResponse(),
            requestHeaders(
                headerWithName("Authorization").description("Access Token"))
        ));
    }

    @Test
    @DisplayName("내가 쓴 글 조회")
    @WithMockUser
    public void getMyPosts() throws Exception {
        //given
        PostInfo post1 = PostInfo.builder()
            .postId(1L)
            .title("제목1")
            .createdAt(LocalDateTime.now())
            .build();

        PostInfo post2 = PostInfo.builder()
            .postId(1L)
            .title("제목1")
            .createdAt(LocalDateTime.now())
            .build();

        PostInfo post3 = PostInfo.builder()
            .postId(1L)
            .title("제목1")
            .createdAt(LocalDateTime.now())
            .build();

        List<PostInfo> posts = List.of(post1, post2, post3);

        MyPostsResponse response = MyPostsResponse.builder()
            .postCount(posts.size())
            .posts(posts)
            .build();

        //when
        given(memberService.getMyPosts(anyString()))
            .willReturn(response);

        //then
        mockMvc.perform(
                get("/api/mypage/posts")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "accessToken"))
            .andDo(print()).andExpect(status().isOk())
            .andExpect(jsonPath("$.postCount").value(response.getPostCount()))
            .andExpect(jsonPath("$.posts[0].postId").value(post1.getPostId()))
            .andExpect(jsonPath("$.posts[0].title").value(post1.getTitle()))
            .andExpect(jsonPath("$.posts[1].postId").value(post2.getPostId()))
            .andExpect(jsonPath("$.posts[1].title").value(post2.getTitle()))
            .andExpect(jsonPath("$.posts[2].postId").value(post3.getPostId()))
            .andExpect(jsonPath("$.posts[2].title").value(post3.getTitle()))
            .andDo(document("mypage/get-my-posts/success",
                getDocumentRequest(),
                getDocumentResponse(),
                requestHeaders(
                    headerWithName("Authorization").description("Access Token")
                )
            ));
    }

    @Test
    @DisplayName("내가 쓴 글 조회 - 회원 못 찾았을 때")
    @WithMockUser
    public void getMyPosts_ExceptionThrown_MemberNotFound() throws Exception {
        //given
        given(memberService.getMyPosts(anyString()))
            .willThrow(new MemberNotFoundException());

        //when
        //then
        mockMvc.perform(
                get("/api/mypage/posts")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "accessToken"))
            .andExpect(status().is4xxClientError())
            .andExpect(jsonPath("$.code").value(MEMBER_NOT_FOUND.getCode()))
            .andExpect(jsonPath("$.message").value(MEMBER_NOT_FOUND.getMessage()))
            .andDo(document("mypage/get-my-posts/failure/member-not-found",
                getDocumentRequest(),
                getDocumentResponse(),
                requestHeaders(
                    headerWithName("Authorization").description("Access Token")
                )
            ));
    }

}