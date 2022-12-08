package com.gaethering.gaetheringserver.member.controller;

import static com.gaethering.gaetheringserver.member.util.ApiDocumentUtils.getDocumentRequest;
import static com.gaethering.gaetheringserver.member.util.ApiDocumentUtils.getDocumentResponse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
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

import com.gaethering.gaetheringserver.member.dto.FollowResponse;
import com.gaethering.gaetheringserver.member.exception.MemberNotFoundException;
import com.gaethering.gaetheringserver.member.service.FollowService;
import java.security.Principal;
import java.util.List;
import java.util.Objects;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

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
    @WithMockUser
    public void createFollow() throws Exception {
        //given
        Principal principal = Mockito.mock(Principal.class);
        given(principal.getName()).willReturn("test@test.com");

        //when
        //then
        mockMvc.perform(post("/api/members/{memberId}/follow", 1)
                .header("Authorization", "accessToken"))
            .andExpect(status().isCreated())
            .andDo(print())
            .andDo(document("follow/create-follow",
                getDocumentRequest(),
                getDocumentResponse(),
                pathParameters(parameterWithName("memberId").description("팔로우할 회원 id")),
                requestHeaders(
                    headerWithName("Authorization").description("Access Token"))
            ));
    }

    @Test
    @WithMockUser
    @DisplayName("회원 못 찾을 때")
    public void createFollowMemberNotFoundFailure() throws Exception {
        //given

        //when
        when(followService.createFollow("test@test.com", 1L)).thenThrow(
            new MemberNotFoundException());

        //then
        mockMvc.perform(post("/api/members/{memberId}/follow", 1)
                .header("Authorization", "accessToken"))
            .andExpect(status().is4xxClientError())
            .andExpect(result ->
                assertThat(getApiResultExceptionClass(result)).isEqualTo(
                    MemberNotFoundException.class))
            .andDo(print())
            .andDo(document("follow/create-follow-failure",
                getDocumentRequest(),
                getDocumentResponse(),
                pathParameters(parameterWithName("memberId").description("팔로우할 회원 id")),
                requestHeaders(
                    headerWithName("Authorization").description("Access Token"))
            ));
    }

    @Test
    @WithMockUser
    public void getFollowers() throws Exception {
        //given
        List<FollowResponse> followResponses = createFollowResponses();
        given(followService.getFollowers(anyLong()))
            .willReturn(followResponses);

        //when
        //then
        checkPerform("/api/members/{memberId}/follower", "follow/get-followers",
            followResponses.get(0),
            followResponses.get(1));
    }

    @Test
    @WithMockUser
    public void getFollowings() throws Exception {
        //given
        List<FollowResponse> followResponses = createFollowResponses();
        given(followService.getFollowees(anyLong()))
            .willReturn(followResponses);

        //when
        //then
        checkPerform("/api/members/{memberId}/following", "follow/get-followings",
            followResponses.get(0),
            followResponses.get(1));
    }

    @Test
    @WithMockUser
    public void removeFollow() throws Exception {
        //given
        Principal principal = Mockito.mock(Principal.class);
        given(principal.getName()).willReturn("test@test.com");

        //when
        //then
        mockMvc.perform(delete("/api/members/{memberId}/follow", 1)
                .header("Authorization", "accessToken"))
            .andExpect(status().isOk())
            .andDo(print())
            .andDo(document("follow/delete-follow",
                getDocumentRequest(),
                getDocumentResponse(),
                pathParameters(parameterWithName("memberId").description("팔로우 취소할 회원 id")),
                requestHeaders(
                    headerWithName("Authorization").description("Access Token"))
            ));
    }

    private Class<? extends Exception> getApiResultExceptionClass(MvcResult result) {
        return Objects.requireNonNull(result.getResolvedException()).getClass();
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