package com.gaethering.gaetheringserver.member.controller;

import static com.gaethering.gaetheringserver.member.exception.errorcode.FollowErrorCode.FOLLOW_NOT_FOUND;
import static com.gaethering.gaetheringserver.member.exception.errorcode.MemberErrorCode.MEMBER_NOT_FOUND;
import static com.gaethering.gaetheringserver.member.util.ApiDocumentUtils.getDocumentRequest;
import static com.gaethering.gaetheringserver.member.util.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.gaethering.gaetheringserver.member.dto.follow.FollowResponse;
import com.gaethering.gaetheringserver.member.exception.member.MemberNotFoundException;
import com.gaethering.gaetheringserver.member.service.follow.FollowService;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@AutoConfigureRestDocs
class FollowControllerTest {

    @MockBean
    private FollowService followService;
    @Autowired
    private MockMvc mockMvc;


    @Test
    @DisplayName("팔로우 생성 - 회원을 못 찾았을 때")
    @WithMockUser
    public void createFollowMemberNotFoundFailure() throws Exception {
        //given
        given(followService.createFollow(anyString(), anyLong())).willThrow(
            new MemberNotFoundException());

        //when
        //then
        mockMvc.perform(post("/api/members/{memberId}/follow", 1)
                .header("Authorization", "accessToken"))
            .andExpect(status().is4xxClientError())
            .andExpect(jsonPath("$.code").value(MEMBER_NOT_FOUND.getCode()))
            .andExpect(jsonPath("$.message").value(MEMBER_NOT_FOUND.getMessage()))

            .andDo(print())
            .andDo(document("follow/create-follow/failure/member-not-found",
                getDocumentRequest(),
                getDocumentResponse(),
                pathParameters(parameterWithName("memberId").description("팔로우 하려는 회원 Id")),
                requestHeaders(
                    headerWithName("Authorization").description("Access Token"))
            ));
    }


    @Test
    @WithMockUser
    public void createFollowSuccess() throws Exception {
        mockMvc.perform(post("/api/members/{memberId}/follow", 1)
                .header("Authorization", "accessToken"))
            .andExpect(status().isCreated())

            .andDo(print())
            .andDo(document("follow/create-follow/success",
                getDocumentRequest(),
                getDocumentResponse(),
                pathParameters(parameterWithName("memberId").description("팔로우 하려는 회원 id")),
                requestHeaders(
                    headerWithName("Authorization").description("Access Token"))
            ));
    }

    @Test
    @WithMockUser
    @DisplayName("팔로워 목록 - 회원을 못 찾았을 때")
    public void getFollowersMemberNotFoundFailure() throws Exception {
        //given
        given(followService.getFollowers(anyLong()))
            .willThrow(new MemberNotFoundException());

        //when
        //then
        checkGetMemberNotFound("/api/members/{memberId}/follower",
            "follow/get-followers/failure/member-not-found");
    }

    @Test
    @WithMockUser
    public void getFollowersSuccess() throws Exception {
        //given
        List<FollowResponse> followResponses = createFollowResponses();
        given(followService.getFollowers(anyLong()))
            .willReturn(followResponses);

        //when
        //then
        checkPerform("/api/members/{memberId}/follower", "follow/get-followers/success",
            followResponses.get(0),
            followResponses.get(1));
    }

    @Test
    @WithMockUser
    @DisplayName("팔로잉 목록 - 회원을 못 찾았을 때")
    public void getFolloweesMemberNotFoundFailure() throws Exception {
        //given
        given(followService.getFollowees(anyLong()))
            .willThrow(new MemberNotFoundException());

        //when
        //then
        checkGetMemberNotFound("/api/members/{memberId}/following",
            "follow/get-followings/failure/member-not-found");
    }

    @Test
    @WithMockUser
    public void getFollowingsSuccess() throws Exception {
        //given
        List<FollowResponse> followResponses = createFollowResponses();
        given(followService.getFollowees(anyLong()))
            .willReturn(followResponses);

        //when
        //then
        checkPerform("/api/members/{memberId}/following", "follow/get-followings/success",
            followResponses.get(0), followResponses.get(1));
    }

    @Test
    @WithMockUser
    @DisplayName("팔로우 삭제 - 삭제한 팔로우가 1이하 일 때")
    public void removeFollowFollowNotFoundFailure() throws Exception {
        //given
        given(followService.removeFollow(any(), anyLong()))
            .willReturn(false);

        //when
        //then
        mockMvc.perform(delete("/api/members/{memberId}/follow", 1)
                .header("Authorization", "accessToken"))
            .andExpect(status().is4xxClientError())
            .andExpect(jsonPath("$.code").value(FOLLOW_NOT_FOUND.getCode()))
            .andExpect(jsonPath("$.message").value(FOLLOW_NOT_FOUND.getMessage()))

            .andDo(print())
            .andDo(document("follow/delete-follow/failure/follow-not-found",
                getDocumentRequest(),
                getDocumentResponse(),
                pathParameters(parameterWithName("memberId").description("팔로우 취소할 회원 id")),
                requestHeaders(
                    headerWithName("Authorization").description("Access Token"))
            ));
    }

    @Test
    @WithMockUser
    @DisplayName("팔로우 삭제 - 회원 못 찾았을 때")
    public void removeFollowMemberNotFoundFailure() throws Exception {
        //given
        given(followService.removeFollow(anyString(), anyLong()))
            .willThrow(new MemberNotFoundException());

        //when
        //then
        mockMvc.perform(delete("/api/members/{memberId}/follow", 1)
                .header("Authorization", "accessToken"))
            .andExpect(status().is4xxClientError())
            .andExpect(jsonPath("$.code").value(MEMBER_NOT_FOUND.getCode()))
            .andExpect(jsonPath("$.message").value(MEMBER_NOT_FOUND.getMessage()))

            .andDo(print())
            .andDo(document("follow/delete-follow/failure/member-not-found",
                getDocumentRequest(),
                getDocumentResponse(),
                pathParameters(parameterWithName("memberId").description("팔로우 취소할 회원 id")),
                requestHeaders(
                    headerWithName("Authorization").description("Access Token"))
            ));
    }

    @Test
    @WithMockUser
    public void removeFollowSuccess() throws Exception {
        //given
        given(followService.removeFollow(any(), anyLong()))
            .willReturn(true);

        //when
        //then
        mockMvc.perform(delete("/api/members/{memberId}/follow", 1)
                .header("Authorization", "accessToken"))
            .andExpect(status().isOk())

            .andDo(print())
            .andDo(document("follow/delete-follow/success",
                getDocumentRequest(),
                getDocumentResponse(),
                pathParameters(parameterWithName("memberId").description("팔로우 취소할 회원 id")),
                requestHeaders(
                    headerWithName("Authorization").description("Access Token"))
            ));
    }

    private void checkPerform(String url, String identifier, FollowResponse followResponse1,
        FollowResponse followResponse2) throws Exception {
        mockMvc.perform(get(url, 1)
                .header("Authorization", "accessToken"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(followResponse1.getId()))
            .andExpect(jsonPath("$[0].name").value(followResponse1.getName()))
            .andExpect(jsonPath("$[0].nickname").value(followResponse1.getNickname()))
            .andExpect(jsonPath("$[1].id").value(followResponse2.getId()))
            .andExpect(jsonPath("$[1].name").value(followResponse2.getName()))
            .andExpect(jsonPath("$[1].nickname").value(followResponse2.getNickname()))

            .andDo(print())
            .andDo(document(identifier,
                getDocumentRequest(),
                getDocumentResponse(),
                pathParameters(
                    parameterWithName("memberId").description("회원 Id")),
                requestHeaders(
                    headerWithName("Authorization").description("Access Token"))
            ));
    }

    private void checkGetMemberNotFound(String url, String identifier) throws Exception {
        mockMvc.perform(get(url, 1)
                .header("Authorization", "accessToken"))
            .andExpect(status().is4xxClientError())
            .andExpect(jsonPath("$.code").value(MEMBER_NOT_FOUND.getCode()))
            .andExpect(jsonPath("$.message").value(MEMBER_NOT_FOUND.getMessage()))

            .andDo(print())
            .andDo(document(identifier,
                getDocumentRequest(),
                getDocumentResponse(),
                pathParameters(parameterWithName("memberId").description("대상 회원 Id")),
                requestHeaders(
                    headerWithName("Authorization").description("Access Token"))
            ));
    }

    private List<FollowResponse> createFollowResponses() {
        FollowResponse followResponse1 = FollowResponse.builder().id(1L).name("name1")
            .nickname("nickname1")
            .build();
        FollowResponse followResponse2 = FollowResponse.builder().id(2L).name("name2")
            .nickname("nickname2")
            .build();
        return List.of(followResponse1, followResponse2);
    }
}