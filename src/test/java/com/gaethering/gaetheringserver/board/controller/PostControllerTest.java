package com.gaethering.gaetheringserver.board.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gaethering.gaetheringserver.board.dto.PostRequest;
import com.gaethering.gaetheringserver.board.dto.PostResponse;
import com.gaethering.gaetheringserver.board.service.PostService;
import com.gaethering.gaetheringserver.member.exception.member.MemberNotFoundException;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static com.gaethering.gaetheringserver.member.exception.errorcode.MemberErrorCode.MEMBER_NOT_FOUND;
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
class PostControllerTest {

    @MockBean
    private PostService postService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("게시물 작성 성공")
    @WithMockUser
    void writePost_Success() throws Exception {

        MockMultipartFile file1 = new MockMultipartFile("test1", "test1.PNG",
                MediaType.IMAGE_PNG_VALUE, "test1".getBytes(StandardCharsets.UTF_8));

        MockMultipartFile file2 = new MockMultipartFile("test2", "test2.PNG",
                MediaType.IMAGE_PNG_VALUE, "test2".getBytes(StandardCharsets.UTF_8));

        PostRequest request = PostRequest.builder()
                .title("제목입니다")
                .content("내용입니다")
                .categoryId(1L)
                .build();

        PostResponse response = PostResponse.builder()
                .title("제목입니다")
                .content("내용입니다")
                .categoryName("카테고리")
                .nickname("닉네임")
                .imageUrls(List.of("test1", "test2"))
                .heartCnt(0)
                .viewCnt(0)
                .createAt(LocalDateTime.now())
                .build();

        Mockito.when(postService.writePost(anyString(), anyList(), any(PostRequest.class)))
                .thenReturn(response);

        String requestJson = objectMapper.writeValueAsString(request);

        MockPart data = new MockPart("data", requestJson.getBytes());
        data.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/boards")
                        .file("images", file1.getBytes())
                        .file("images", file2.getBytes())
                        .part(data)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .with(csrf())
                        .header("Authorization", "accessToken"))
                .andExpect(jsonPath("$.title").value(response.getTitle()))
                .andExpect(jsonPath("$.content").value(response.getContent()))
                .andExpect(jsonPath("$.imageUrls.[0]").value(response.getImageUrls().get(0)))
                .andExpect(
                        jsonPath("$.categoryName").value(String.valueOf(response.getCategoryName())))
                .andExpect(
                        jsonPath("$.viewCnt").value(String.valueOf(response.getViewCnt())))
                .andExpect(
                        jsonPath("$.heartCnt").value(String.valueOf(response.getHeartCnt())))
                .andExpect(jsonPath("$.nickname").value(String.valueOf(response.getNickname())))
                .andExpect(status().isCreated())
                .andDo(print())
                .andDo(document("boards/write-post/success",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestHeaders(
                                headerWithName("Authorization").description("Access Token"))
                ));
    }

    @Test
    @DisplayName("게시물 작성 실패 - 회원 없음")
    @WithMockUser
    void writePost_fail() throws Exception {

        MockMultipartFile file1 = new MockMultipartFile("test1", "test1.PNG",
                MediaType.IMAGE_PNG_VALUE, "test1".getBytes(StandardCharsets.UTF_8));

        MockMultipartFile file2 = new MockMultipartFile("test2", "test2.PNG",
                MediaType.IMAGE_PNG_VALUE, "test2".getBytes(StandardCharsets.UTF_8));

        PostRequest request = PostRequest.builder()
                .title("제목입니다")
                .content("내용입니다")
                .categoryId(1L)
                .build();

        given(postService.writePost(anyString(), anyList(), any(PostRequest.class)))
                .willThrow(new MemberNotFoundException());

        String requestJson = objectMapper.writeValueAsString(request);

        MockPart data = new MockPart("data", requestJson.getBytes());
        data.getHeaders().setContentType(MediaType.APPLICATION_JSON);


        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/boards")
                        .file("images", file1.getBytes())
                        .file("images", file2.getBytes())
                        .part(data)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .header("Authorization", "accessToken"))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.code").value(MEMBER_NOT_FOUND.getCode()))
                .andExpect(jsonPath("$.message").value(MEMBER_NOT_FOUND.getMessage()))

                .andDo(print())
                .andDo(document("boards/write-post/failure/member-not-found",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestHeaders(
                                headerWithName("Authorization").description("Access Token"))
                ));
    }
}