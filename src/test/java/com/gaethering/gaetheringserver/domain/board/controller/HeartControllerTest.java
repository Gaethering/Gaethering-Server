package com.gaethering.gaetheringserver.domain.board.controller;

import com.gaethering.gaetheringserver.domain.board.dto.HeartResponse;
import com.gaethering.gaetheringserver.domain.board.exception.PostNotFoundException;
import com.gaethering.gaetheringserver.domain.board.service.HeartService;
import com.gaethering.gaetheringserver.domain.member.exception.member.MemberNotFoundException;
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

import static com.gaethering.gaetheringserver.domain.board.exception.errorCode.PostErrorCode.*;
import static com.gaethering.gaetheringserver.domain.member.exception.errorcode.MemberErrorCode.MEMBER_NOT_FOUND;
import static com.gaethering.gaetheringserver.member.util.ApiDocumentUtils.getDocumentRequest;
import static com.gaethering.gaetheringserver.member.util.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@AutoConfigureRestDocs
class HeartControllerTest {

    @MockBean
    private HeartService heartService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("좋아요 누르기 성공")
    @WithMockUser
    void pushHeart_Success () throws Exception {

        HeartResponse response = HeartResponse.builder()
                .postId(1L)
                .memberId(2L)
                .likeCnt(10)
                .build();

        given(heartService.pushHeart(anyLong(), anyString()))
                .willReturn(response);

        mockMvc.perform(post("/api/boards/{postId}/hearts", 1L)
                        .with(csrf())
                        .header("Authorization", "accessToken"))
                .andExpect(jsonPath("$.postId").value(String.valueOf(response.getPostId())))
                .andExpect(jsonPath("$.memberId").value(String.valueOf(response.getMemberId())))
                .andExpect(jsonPath("$.likeCnt").value(String.valueOf(response.getLikeCnt())))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("boards/hearts/push-heart/success",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(parameterWithName("postId").description("좋아요 누르고자 하는 게시물 id")),
                        requestHeaders(
                                headerWithName("Authorization").description("Access Token"))
                ));
    }

    @Test
    @DisplayName("좋아요 누르기 실패 - 회원 없음")
    @WithMockUser
    void pushHeart_fail_NoUser () throws Exception {

        given(heartService.pushHeart(anyLong(), anyString()))
                .willThrow(new MemberNotFoundException());

        mockMvc.perform(post("/api/boards/{postId}/hearts", 1L)
                        .with(csrf())
                        .header("Authorization", "accessToken"))
                .andExpect(jsonPath("$.code").value(MEMBER_NOT_FOUND.getCode()))
                .andExpect(jsonPath("$.message").value(MEMBER_NOT_FOUND.getMessage()))
                .andExpect(status().is4xxClientError())
                .andDo(print())
                .andDo(document("boards/hearts/push-heart/failure/member-not-found",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(parameterWithName("postId").description("좋아요 누르고자 하는 게시물 id")),
                        requestHeaders(
                                headerWithName("Authorization").description("Access Token"))
                ));
    }

    @Test
    @DisplayName("좋아요 누르기 실패 - 게시물 없음")
    @WithMockUser
    void pushHeart_fail_NoPost () throws Exception {

        given(heartService.pushHeart(anyLong(), anyString()))
                .willThrow(new PostNotFoundException());

        mockMvc.perform(post("/api/boards/{postId}/hearts", 1L)
                        .with(csrf())
                        .header("Authorization", "accessToken"))
                .andExpect(jsonPath("$.code").value(POST_NOT_FOUND.getCode()))
                .andExpect(jsonPath("$.message").value(POST_NOT_FOUND.getMessage()))
                .andExpect(status().is4xxClientError())
                .andDo(print())
                .andDo(document("boards/hearts/push-heart/failure/post-not-found",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(parameterWithName("postId").description("좋아요 누르고자 하는 게시물 id")),
                        requestHeaders(
                                headerWithName("Authorization").description("Access Token"))
                ));
    }
}