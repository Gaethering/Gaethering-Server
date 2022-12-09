package com.gaethering.gaetheringserver.board.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gaethering.gaetheringserver.board.domain.Category;
import com.gaethering.gaetheringserver.board.dto.PostRequest;
import com.gaethering.gaetheringserver.board.service.PostService;
import com.gaethering.gaetheringserver.config.SecurityConfig;
import com.gaethering.gaetheringserver.filter.JwtAuthenticationFilter;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PostController.class, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
                classes = {SecurityConfig.class, JwtAuthenticationFilter.class})
})
@ActiveProfiles("test")
class PostControllerTest {

    @MockBean
    private PostService postService;
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("게시물 작성 api test - 이미지 있음")
    @WithMockUser
    public void imageUploadTest () throws Exception {

        String email = "test@gmail.com";
        Principal principal = Mockito.mock(Principal.class);
        given(principal.getName()).willReturn(email);

        Category category = Category.builder()
                .id(1L)
                .categoryName("카테고리")
                .build();

        PostRequest request = PostRequest.builder()
                .title("제목입니다")
                .content("내용입니다")
                .category(category)
                .build();

        List<MultipartFile> imageFiles = List.of(
                new MockMultipartFile("test1", "test1.PNG",
                        MediaType.IMAGE_PNG_VALUE, "test1".getBytes(StandardCharsets.UTF_8)),
                new MockMultipartFile("test2", "test2.PNG",
                        MediaType.IMAGE_PNG_VALUE, "test2".getBytes(StandardCharsets.UTF_8))
        );


        String requestJson = objectMapper.writeValueAsString(request);

        MockMultipartFile post = new MockMultipartFile("data", "data",
                "application/json", requestJson.getBytes(StandardCharsets.UTF_8));


        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/boards")
                .file("images", imageFiles.get(0).getBytes())
                .file("images", imageFiles.get(1).getBytes())
                .file(post)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .with(csrf()))
                .andDo(print())
                .andExpect(status().isCreated()).andReturn();
    }

    @Test
    @DisplayName("게시물 작성 api test - 이미지 없음")
    @WithMockUser
    public void postUploadTest () throws Exception {
        Category category = Category.builder()
                .id(1L)
                .categoryName("카테고리")
                .build();

        PostRequest request = PostRequest.builder()
                .title("제목입니다")
                .content("내용입니다")
                .category(category)
                .build();

        String requestJson = objectMapper.writeValueAsString(request);

        MockMultipartFile post = new MockMultipartFile("data", "data",
                "application/json", requestJson.getBytes(StandardCharsets.UTF_8));

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/boards")
                        .file(post)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isCreated()).andReturn();
    }
}