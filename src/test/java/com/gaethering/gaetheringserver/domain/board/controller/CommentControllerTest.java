package com.gaethering.gaetheringserver.domain.board.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gaethering.gaetheringserver.domain.board.dto.CommentRequest;
import com.gaethering.gaetheringserver.domain.board.dto.CommentResponse;
import com.gaethering.gaetheringserver.domain.board.exception.CommentNotFoundException;
import com.gaethering.gaetheringserver.domain.board.exception.NoPermissionDeleteCommentException;
import com.gaethering.gaetheringserver.domain.board.exception.NoPermissionUpdateCommentException;
import com.gaethering.gaetheringserver.domain.board.exception.PostNotFoundException;
import com.gaethering.gaetheringserver.domain.board.service.CommentService;
import com.gaethering.gaetheringserver.domain.member.exception.member.MemberNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;

import static com.gaethering.gaetheringserver.domain.board.exception.errorCode.PostErrorCode.*;
import static com.gaethering.gaetheringserver.domain.member.exception.errorcode.MemberErrorCode.MEMBER_NOT_FOUND;
import static com.gaethering.gaetheringserver.member.util.ApiDocumentUtils.getDocumentRequest;
import static com.gaethering.gaetheringserver.member.util.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@AutoConfigureRestDocs
class CommentControllerTest {

    @MockBean
    private CommentService commentService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("댓글 작성 성공")
    @WithMockUser
    void writeComment_Success () throws Exception {

        CommentRequest request = CommentRequest.builder()
                .content("댓글입니다")
                .build();

        LocalDateTime date = LocalDateTime.of(2022, 12, 31, 23, 59, 59);

        CommentResponse response = CommentResponse.builder()
                .content("댓글입니다")
                .nickname("닉네임")
                .createAt(date)
                .build();

        Mockito.when(commentService.writeComment(anyString(), anyLong(), any(CommentRequest.class)))
                .thenReturn(response);

        String requestJson = objectMapper.writeValueAsString(request);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/boards/{postId}/comments", 1L)
                        .content(requestJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .header("Authorization", "accessToken"))
                .andExpect(jsonPath("$.comment").value(response.getContent()))
                .andExpect(jsonPath("$.nickname").value(response.getNickname()))
                .andExpect(jsonPath("$.createAt").value(String.valueOf(response.getCreateAt())))
                .andExpect(status().isCreated())
                .andDo(print())
                .andDo(document("boards/comments/write-comment/success",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestHeaders(
                                headerWithName("Authorization").description("Access Token"))
                ));
    }
    @Test
    @DisplayName("댓글 작성 실패 - 회원 없음")
    @WithMockUser
    void write_Comment_fail_NoUser () throws Exception {

        CommentRequest request = CommentRequest.builder()
                .content("댓글입니다")
                .build();

        given(commentService.writeComment(anyString(), anyLong(), any(CommentRequest.class)))
                .willThrow(new MemberNotFoundException());

        String requestJson = objectMapper.writeValueAsString(request);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/boards/{postId}/comments", 1L)
                        .content(requestJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .header("Authorization", "accessToken"))
                .andExpect(jsonPath("$.code").value(MEMBER_NOT_FOUND.getCode()))
                .andExpect(jsonPath("$.message").value(MEMBER_NOT_FOUND.getMessage()))
                .andExpect(status().is4xxClientError())
                .andDo(print())
                .andDo(document("boards/comments/write-comment/failure/member-not-found",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestHeaders(
                                headerWithName("Authorization").description("Access Token"))
                ));
    }
    @Test
    @DisplayName("댓글 작성 실패 - 게시물 없음")
    @WithMockUser
    void write_Comment_fail_NoPost () throws Exception {

        CommentRequest request = CommentRequest.builder()
                .content("댓글입니다")
                .build();

        given(commentService.writeComment(anyString(), anyLong(), any(CommentRequest.class)))
                .willThrow(new PostNotFoundException());

        String requestJson = objectMapper.writeValueAsString(request);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/boards/{postId}/comments", 1L)
                        .content(requestJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .header("Authorization", "accessToken"))
                .andExpect(jsonPath("$.code").value(POST_NOT_FOUND.getCode()))
                .andExpect(jsonPath("$.message").value(POST_NOT_FOUND.getMessage()))
                .andExpect(status().is4xxClientError())
                .andDo(print())
                .andDo(document("boards/comments/write-comment/failure/post-not-found",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestHeaders(
                                headerWithName("Authorization").description("Access Token"))
                ));
    }

    @Test
    @DisplayName("댓글 삭제 성공")
    @WithMockUser
    void deleteComment_Success () throws Exception {

        Mockito.when(commentService.deleteComment(anyString(), anyLong(), anyLong()))
                .thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/boards/{postId}/comments/{commentId}", 1L, 1L)
                        .with(csrf())
                        .header("Authorization", "accessToken"))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("boards/comments/delete-comment/success",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestHeaders(
                                headerWithName("Authorization").description("Access Token"))
                ));
    }

    @Test
    @DisplayName("댓글 삭제 실패 - 회원 없음")
    @WithMockUser
    void delete_Comment_fail_NoUser () throws Exception {

        given(commentService.deleteComment(anyString(), anyLong(), anyLong()))
                .willThrow(new MemberNotFoundException());

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/boards/{postId}/comments/{commentId}", 1L, 1L)
                        .with(csrf())
                        .header("Authorization", "accessToken"))
                .andExpect(jsonPath("$.code").value(MEMBER_NOT_FOUND.getCode()))
                .andExpect(jsonPath("$.message").value(MEMBER_NOT_FOUND.getMessage()))
                .andExpect(status().is4xxClientError())
                .andDo(print())
                .andDo(document("boards/comments/delete-comment/failure/member-not-found",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestHeaders(
                                headerWithName("Authorization").description("Access Token"))
                ));
    }

    @Test
    @DisplayName("댓글 삭제 실패 - 게시물 없음")
    @WithMockUser
    void delete_Comment_fail_NoPost () throws Exception {

        given(commentService.deleteComment(anyString(), anyLong(), anyLong()))
                .willThrow(new PostNotFoundException());

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/boards/{postId}/comments/{commentId}", 1L, 1L)
                        .with(csrf())
                        .header("Authorization", "accessToken"))
                .andExpect(jsonPath("$.code").value(POST_NOT_FOUND.getCode()))
                .andExpect(jsonPath("$.message").value(POST_NOT_FOUND.getMessage()))
                .andExpect(status().is4xxClientError())
                .andDo(print())
                .andDo(document("boards/comments/delete-comment/failure/post-not-found",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestHeaders(
                                headerWithName("Authorization").description("Access Token"))
                ));
    }

    @Test
    @DisplayName("댓글 삭제 실패 - 댓글 없음")
    @WithMockUser
    void delete_Comment_fail_NoComment () throws Exception {

        given(commentService.deleteComment(anyString(), anyLong(), anyLong()))
                .willThrow(new CommentNotFoundException());

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/boards/{postId}/comments/{commentId}", 1L, 1L)
                        .with(csrf())
                        .header("Authorization", "accessToken"))
                .andExpect(jsonPath("$.code").value(COMMENT_NOT_FOUND.getCode()))
                .andExpect(jsonPath("$.message").value(COMMENT_NOT_FOUND.getMessage()))
                .andExpect(status().is4xxClientError())
                .andDo(print())
                .andDo(document("boards/comments/delete-comment/failure/comment-not-found",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestHeaders(
                                headerWithName("Authorization").description("Access Token"))
                ));
    }

    @Test
    @DisplayName("댓글 삭제 실패 - 삭제 권한 없음")
    @WithMockUser
    void delete_Comment_fail_UNMATCH_writer () throws Exception {

        given(commentService.deleteComment(anyString(), anyLong(), anyLong()))
                .willThrow(new NoPermissionDeleteCommentException());

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/boards/{postId}/comments/{commentId}", 1L, 1L)
                        .with(csrf())
                        .header("Authorization", "accessToken"))
                .andExpect(jsonPath("$.code").value(NO_PERMISSION_TO_DELETE_COMMENT.getCode()))
                .andExpect(jsonPath("$.message").value(NO_PERMISSION_TO_DELETE_COMMENT.getMessage()))
                .andExpect(status().is4xxClientError())
                .andDo(print())
                .andDo(document("boards/comments/delete-comment/failure/un-match-writer",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestHeaders(
                                headerWithName("Authorization").description("Access Token"))
                ));
    }

    @Test
    @DisplayName("댓글 수정 성공")
    @WithMockUser
    void updateComment_Success () throws Exception {

        CommentRequest request = CommentRequest.builder()
                .content("수정 댓글입니다")
                .build();

        LocalDateTime date = LocalDateTime.of(2022, 12, 31, 23, 59, 59);

        CommentResponse response = CommentResponse.builder()
                .content("수정 댓글입니다")
                .nickname("닉네임")
                .createAt(date)
                .build();

        Mockito.when(commentService.updateComment(anyString(), anyLong(), anyLong(), any(CommentRequest.class)))
                .thenReturn(response);

        String requestJson = objectMapper.writeValueAsString(request);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/boards/{postId}/comments/{commentId}", 1L, 1L)
                        .content(requestJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .header("Authorization", "accessToken"))
                .andExpect(jsonPath("$.comment").value(response.getContent()))
                .andExpect(jsonPath("$.nickname").value(response.getNickname()))
                .andExpect(jsonPath("$.createAt").value(String.valueOf(response.getCreateAt())))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("boards/comments/update-comment/success",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestHeaders(
                                headerWithName("Authorization").description("Access Token"))
                ));
    }

    @Test
    @DisplayName("댓글 수정 실패 - 회원 없음")
    @WithMockUser
    void update_Comment_fail_NoUser () throws Exception {

        CommentRequest request = CommentRequest.builder()
                .content("댓글입니다")
                .build();

        given(commentService.updateComment(anyString(), anyLong(), anyLong(), any(CommentRequest.class)))
                .willThrow(new MemberNotFoundException());

        String requestJson = objectMapper.writeValueAsString(request);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/boards/{postId}/comments/{commentId}", 1L, 1L)
                        .content(requestJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .header("Authorization", "accessToken"))
                .andExpect(jsonPath("$.code").value(MEMBER_NOT_FOUND.getCode()))
                .andExpect(jsonPath("$.message").value(MEMBER_NOT_FOUND.getMessage()))
                .andExpect(status().is4xxClientError())
                .andDo(print())
                .andDo(document("boards/comments/update-comment/failure/member-not-found",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestHeaders(
                                headerWithName("Authorization").description("Access Token"))
                ));
    }

    @Test
    @DisplayName("댓글 수정 실패 - 게시물 없음")
    @WithMockUser
    void update_Comment_fail_NoPost () throws Exception {

        CommentRequest request = CommentRequest.builder()
                .content("댓글입니다")
                .build();

        given(commentService.updateComment(anyString(), anyLong(), anyLong(), any(CommentRequest.class)))
                .willThrow(new PostNotFoundException());

        String requestJson = objectMapper.writeValueAsString(request);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/boards/{postId}/comments/{commentId}", 1L, 1L)
                        .content(requestJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .header("Authorization", "accessToken"))
                .andExpect(jsonPath("$.code").value(POST_NOT_FOUND.getCode()))
                .andExpect(jsonPath("$.message").value(POST_NOT_FOUND.getMessage()))
                .andExpect(status().is4xxClientError())
                .andDo(print())
                .andDo(document("boards/comments/update-comment/failure/post-not-found",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestHeaders(
                                headerWithName("Authorization").description("Access Token"))
                ));
    }

    @Test
    @DisplayName("댓글 수정 실패 - 댓글 없음")
    @WithMockUser
    void update_Comment_fail_NoComment () throws Exception {

        CommentRequest request = CommentRequest.builder()
                .content("댓글입니다")
                .build();

        given(commentService.updateComment(anyString(), anyLong(), anyLong(), any(CommentRequest.class)))
                .willThrow(new CommentNotFoundException());

        String requestJson = objectMapper.writeValueAsString(request);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/boards/{postId}/comments/{commentId}", 1L, 1L)
                        .content(requestJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .header("Authorization", "accessToken"))
                .andExpect(jsonPath("$.code").value(COMMENT_NOT_FOUND.getCode()))
                .andExpect(jsonPath("$.message").value(COMMENT_NOT_FOUND.getMessage()))
                .andExpect(status().is4xxClientError())
                .andDo(print())
                .andDo(document("boards/comments/update-comment/failure/comment-not-found",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestHeaders(
                                headerWithName("Authorization").description("Access Token"))
                ));
    }

    @Test
    @DisplayName("댓글 수정 실패 - 수정 권한 없음")
    @WithMockUser
    void update_Comment_fail_UNMATCH_writer () throws Exception {

        CommentRequest request = CommentRequest.builder()
                .content("댓글입니다")
                .build();

        given(commentService.updateComment(anyString(), anyLong(), anyLong(), any(CommentRequest.class)))
                .willThrow(new NoPermissionUpdateCommentException());

        String requestJson = objectMapper.writeValueAsString(request);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/boards/{postId}/comments/{commentId}", 1L, 1L)
                        .content(requestJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .header("Authorization", "accessToken"))
                .andExpect(jsonPath("$.code").value(NO_PERMISSION_TO_UPDATE_COMMENT.getCode()))
                .andExpect(jsonPath("$.message").value(NO_PERMISSION_TO_UPDATE_COMMENT.getMessage()))
                .andExpect(status().is4xxClientError())
                .andDo(print())
                .andDo(document("boards/comments/update-comment/failure/un-match-writer",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestHeaders(
                                headerWithName("Authorization").description("Access Token"))
                ));
    }
}