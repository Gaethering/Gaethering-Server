package com.gaethering.gaetheringserver.domain.board.controller;

import static com.gaethering.gaetheringserver.domain.board.exception.errorCode.PostErrorCode.CATEGORY_NOT_FOUND;
import static com.gaethering.gaetheringserver.domain.board.exception.errorCode.PostErrorCode.NO_PERMISSION_TO_DELETE_POST;
import static com.gaethering.gaetheringserver.domain.board.exception.errorCode.PostErrorCode.NO_PERMISSION_TO_UPDATE_POST;
import static com.gaethering.gaetheringserver.domain.board.exception.errorCode.PostErrorCode.POST_NOT_FOUND;
import static com.gaethering.gaetheringserver.domain.member.exception.errorcode.MemberErrorCode.MEMBER_NOT_FOUND;
import static com.gaethering.gaetheringserver.member.util.ApiDocumentUtils.getDocumentRequest;
import static com.gaethering.gaetheringserver.member.util.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gaethering.gaetheringserver.domain.board.dto.*;
import com.gaethering.gaetheringserver.domain.board.dto.PostUpdateResponse.PostImageUrlResponse;
import com.gaethering.gaetheringserver.domain.board.exception.CategoryNotFoundException;
import com.gaethering.gaetheringserver.domain.board.exception.NoPermissionDeletePostException;
import com.gaethering.gaetheringserver.domain.board.exception.NoPermissionUpdatePostException;
import com.gaethering.gaetheringserver.domain.board.exception.PostNotFoundException;
import com.gaethering.gaetheringserver.domain.board.service.PostService;
import com.gaethering.gaetheringserver.domain.member.exception.member.MemberNotFoundException;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

		PostWriteRequest request = PostWriteRequest.builder()
			.title("제목입니다")
			.content("내용입니다")
			.categoryId(1L)
			.build();


		PostWriteImageUrlResponse response1 = PostWriteImageUrlResponse.builder()
				.imageUrl("https://test1")
				.representative(true)
				.build();

		PostWriteImageUrlResponse response2 = PostWriteImageUrlResponse.builder()
				.imageUrl("https://test2")
				.representative(false)
				.build();

		LocalDateTime date = LocalDateTime.of(2020, 12, 31, 23, 59, 59);

		PostWriteResponse response = PostWriteResponse.builder()
			.title("제목입니다")
			.content("내용입니다")
			.categoryName("카테고리")
			.nickname("닉네임")
			.imageUrls(List.of(response1, response2))
			.heartCnt(0)
			.viewCnt(0)
			.createdAt(date)
			.build();

		Mockito.when(postService.writePost(anyString(), anyList(), any(PostWriteRequest.class)))
			.thenReturn(response);

		String requestJson = objectMapper.writeValueAsString(request);

		MockPart data = new MockPart("data", requestJson.getBytes());
		data.getHeaders().setContentType(MediaType.APPLICATION_JSON);

		mockMvc.perform(multipart("/api/boards")
				.file("images", file1.getBytes())
				.file("images", file2.getBytes())
				.part(data)
				.contentType(MediaType.MULTIPART_FORM_DATA)
				.with(csrf())
				.header("Authorization", "accessToken"))
			.andExpect(jsonPath("$.title").value(response.getTitle()))
			.andExpect(jsonPath("$.content").value(response.getContent()))
			.andExpect(jsonPath("$.imageUrls[0].imageUrl").value(
						response.getImageUrls().get(0).getImageUrl()))
			.andExpect(jsonPath("$.imageUrls[0].isRepresentative").value(
						response.getImageUrls().get(0).isRepresentative()))
			.andExpect(
				jsonPath("$.categoryName").value(String.valueOf(response.getCategoryName())))
			.andExpect(
				jsonPath("$.viewCnt").value(String.valueOf(response.getViewCnt())))
			.andExpect(
				jsonPath("$.heartCnt").value(String.valueOf(response.getHeartCnt())))
			.andExpect(jsonPath("$.nickname").value(response.getNickname()))
			.andExpect(jsonPath("$.createdAt").value(String.valueOf(response.getCreatedAt())))
			.andExpect(status().isCreated())
			.andDo(print())
			.andDo(document("boards/write-post/success",
				getDocumentRequest(),
				getDocumentResponse(),
				requestHeaders(
					headerWithName("Authorization").description("Access Token")),
					relaxedRequestParts(
							partWithName("images").description("게시물 이미지들"),
							partWithName("data").description("게시물 제목 및 내용")
					)
			));
	}

	@Test
	@DisplayName("게시물 작성 실패 - 회원 없음")
	@WithMockUser
	void writePost_fail_NoUser() throws Exception {

		MockMultipartFile file1 = new MockMultipartFile("test1", "test1.PNG",
			MediaType.IMAGE_PNG_VALUE, "test1".getBytes(StandardCharsets.UTF_8));

		MockMultipartFile file2 = new MockMultipartFile("test2", "test2.PNG",
			MediaType.IMAGE_PNG_VALUE, "test2".getBytes(StandardCharsets.UTF_8));

		PostWriteRequest request = PostWriteRequest.builder()
			.title("제목입니다")
			.content("내용입니다")
			.categoryId(1L)
			.build();

		given(postService.writePost(anyString(), anyList(), any(PostWriteRequest.class)))
			.willThrow(new MemberNotFoundException());

		String requestJson = objectMapper.writeValueAsString(request);

		MockPart data = new MockPart("data", requestJson.getBytes());
		data.getHeaders().setContentType(MediaType.APPLICATION_JSON);

		mockMvc.perform(multipart("/api/boards")
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
					headerWithName("Authorization").description("Access Token")),
					relaxedRequestParts(
							partWithName("images").description("게시물 이미지들"),
							partWithName("data").description("게시물 제목 및 내용")
					)
			));
	}

	@Test
	@DisplayName("게시물 작성 실패 - 카테고리 없음")
	@WithMockUser
	void writePost_fail_NoCategory() throws Exception {
		MockMultipartFile file1 = new MockMultipartFile("test1", "test1.PNG",
			MediaType.IMAGE_PNG_VALUE, "test1".getBytes(StandardCharsets.UTF_8));

		MockMultipartFile file2 = new MockMultipartFile("test2", "test2.PNG",
			MediaType.IMAGE_PNG_VALUE, "test2".getBytes(StandardCharsets.UTF_8));

		PostWriteRequest request = PostWriteRequest.builder()
			.title("제목입니다")
			.content("내용입니다")
			.categoryId(1L)
			.build();

		given(postService.writePost(anyString(), anyList(), any(PostWriteRequest.class)))
			.willThrow(new CategoryNotFoundException());

		String requestJson = objectMapper.writeValueAsString(request);

		MockPart data = new MockPart("data", requestJson.getBytes());
		data.getHeaders().setContentType(MediaType.APPLICATION_JSON);

		mockMvc.perform(multipart("/api/boards")
				.file("images", file1.getBytes())
				.file("images", file2.getBytes())
				.part(data)
				.contentType(MediaType.MULTIPART_FORM_DATA)
				.header("Authorization", "accessToken"))
			.andExpect(status().is4xxClientError())
			.andExpect(jsonPath("$.code").value(CATEGORY_NOT_FOUND.getCode()))
			.andExpect(jsonPath("$.message").value(CATEGORY_NOT_FOUND.getMessage()))

			.andDo(print())
			.andDo(document("boards/write-post/failure/category-not-found",
				getDocumentRequest(),
				getDocumentResponse(),
				requestHeaders(
					headerWithName("Authorization").description("Access Token")),
					relaxedRequestParts(
							partWithName("images").description("게시물 이미지들"),
							partWithName("data").description("게시물 제목 및 내용")
					)
			));
	}

	@Test
	@DisplayName("게시물 수정 실패 - 회원 찾을 수 없는 경우")
	@WithMockUser
	void updatePostFailure_MemberNotFound() throws Exception {
		//given
		PostUpdateRequest request = PostUpdateRequest.builder()
			.title("게시글 제목 수정")
			.content("게시글 수정 내용입니다.")
			.build();
		given(postService.updatePost(anyString(), anyLong(), any()))
			.willThrow(new MemberNotFoundException());

		//when
		//then
		mockMvc.perform(patch("/api/boards/{postId}", 1)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "accessToken")
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().is4xxClientError())
			.andExpect(jsonPath("$.code").value(MEMBER_NOT_FOUND.getCode()))
			.andExpect(jsonPath("$.message").value(MEMBER_NOT_FOUND.getMessage()))
			.andDo(print())
			.andDo(document("boards/update-post/failure/member-not-found",
				getDocumentRequest(),
				getDocumentResponse(),
				pathParameters(parameterWithName("postId").description("수정할 게시글 Id")),
				requestHeaders(
					headerWithName("Authorization").description("Access Token"))
			));
	}

	@Test
	@DisplayName("게시물 수정 실패 - 게시물 찾을 수 없는 경우")
	@WithMockUser
	void updatePostFailure_PostNotFound() throws Exception {
		//given
		PostUpdateRequest request = PostUpdateRequest.builder()
			.title("게시글 제목 수정")
			.content("게시글 수정 내용입니다.")
			.build();
		given(postService.updatePost(anyString(), anyLong(), any()))
			.willThrow(new PostNotFoundException());

		//when
		//then
		mockMvc.perform(patch("/api/boards/{postId}", 1)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "accessToken")
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().is4xxClientError())
			.andExpect(jsonPath("$.code").value(POST_NOT_FOUND.getCode()))
			.andExpect(jsonPath("$.message").value(POST_NOT_FOUND.getMessage()))
			.andDo(print())
			.andDo(document("boards/update-post/failure/post-not-found",
				getDocumentRequest(),
				getDocumentResponse(),
				pathParameters(parameterWithName("postId").description("수정할 게시글 Id")),
				requestHeaders(
					headerWithName("Authorization").description("Access Token"))
			));
	}

	@Test
	@DisplayName("게시물 수정 실패 - 게시물 작성자가 아닐 경우")
	@WithMockUser
	void updatePostFailure_NoPermissionUpdatePost() throws Exception {
		//given
		PostUpdateRequest request = PostUpdateRequest.builder()
			.title("게시글 제목 수정")
			.content("게시글 수정 내용입니다.")
			.build();
		given(postService.updatePost(anyString(), anyLong(), any()))
			.willThrow(new NoPermissionUpdatePostException());

		//when
		//then
		mockMvc.perform(patch("/api/boards/{postId}", 1)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "accessToken")
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().is4xxClientError())
			.andExpect(jsonPath("$.code").value(NO_PERMISSION_TO_UPDATE_POST.getCode()))
			.andExpect(jsonPath("$.message").value(NO_PERMISSION_TO_UPDATE_POST.getMessage()))
			.andDo(print())
			.andDo(document("boards/update-post/failure/no-permission-update-post",
				getDocumentRequest(),
				getDocumentResponse(),
				pathParameters(parameterWithName("postId").description("수정할 게시글 Id")),
				requestHeaders(
					headerWithName("Authorization").description("Access Token"))
			));
	}

	@Test
	@DisplayName("게시물 수정 성공")
	@WithMockUser
	void updatePostSuccess() throws Exception {
		LocalDateTime date = LocalDateTime.of(2020, 12, 31, 23, 59, 59);

		PostUpdateRequest request = PostUpdateRequest.builder()
			.title("게시글 제목 수정")
			.content("게시글 수정 내용입니다.")
			.build();

		PostImageUrlResponse postImageUrlResponse = PostImageUrlResponse.builder()
			.imageId(1L)
			.imageUrl("https://test~")
			.representative(false)
			.createdAt(date)
			.updatedAt(date)
			.build();

		PostUpdateResponse response = PostUpdateResponse.builder()
			.title("게시글 제목 수정")
			.content("게시글 수정 내용입니다.")
			.categoryName("질문 있어요")
			.heartCnt(0)
			.viewCnt(0)
			.nickname("닉네임")
			.imageUrls(List.of(postImageUrlResponse))
			.createdAt(date)
			.updatedAt(date)
			.build();

		given(postService.updatePost(anyString(), anyLong(), any()))
			.willReturn(response);

		String requestString = objectMapper.writeValueAsString(request);

		mockMvc.perform(patch("/api/boards/{postId}", 1)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "accessToken")
				.content(requestString))

			.andExpect(status().isOk())
			.andExpect(jsonPath("$.title").value(response.getTitle()))
			.andExpect(jsonPath("$.content").value(response.getContent()))
			.andExpect(jsonPath("$.categoryName").value(response.getCategoryName()))
			.andExpect(jsonPath("$.heartCnt").value(response.getHeartCnt()))
			.andExpect(jsonPath("$.viewCnt").value(String.valueOf(response.getViewCnt())))
			.andExpect(jsonPath("$.nickname").value(response.getNickname()))
			.andExpect(jsonPath("$.imageUrls[0].imageId").value(
				response.getImageUrls().get(0).getImageId()))
			.andExpect(jsonPath("$.imageUrls[0].imageUrl").value(
				response.getImageUrls().get(0).getImageUrl()))
			.andExpect(jsonPath("$.imageUrls[0].isRepresentative").value(
				response.getImageUrls().get(0).isRepresentative()))
			.andExpect(jsonPath("$.imageUrls[0].createdAt").value(
				response.getImageUrls().get(0).getCreatedAt().toString()))
			.andExpect(jsonPath("$.imageUrls[0].updatedAt").value(
				response.getImageUrls().get(0).getUpdatedAt().toString()))
			.andExpect(jsonPath("$.createdAt").value(response.getCreatedAt().toString()))
			.andExpect(jsonPath("$.updatedAt").value(response.getUpdatedAt().toString()))
			.andDo(print())
			.andDo(document("boards/update-post/success",
				getDocumentRequest(),
				getDocumentResponse(),
				pathParameters(parameterWithName("postId").description("수정할 게시글 Id")),
				requestHeaders(
					headerWithName("Authorization").description("Access Token"))
			));
	}

	@Test
	@DisplayName("게시물 이미지 업로드 실패 - 회원 찾을 수 없는 경우")
	@WithMockUser
	void uploadPostImageFailure_MemberNotFound() throws Exception {
		//given
		String email = "test@test.com";
		Principal principal = Mockito.mock(Principal.class);
		given(principal.getName()).willReturn(email);

		String filename = "test.png";
		String contentType = "image/png";
		MockMultipartFile file = new MockMultipartFile("image", filename, contentType,
			"test".getBytes());
		given(postService.uploadPostImage(anyString(), anyLong(), any()))
			.willThrow(new MemberNotFoundException());

		//when
		//then
        mockMvc.perform(multipart("/api/boards/{postId}/images", 1)
                .file(file)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .header("Authorization", "accessToken"))
            .andExpect(status().is4xxClientError())
            .andExpect(jsonPath("$.code").value(MEMBER_NOT_FOUND.getCode()))
            .andExpect(jsonPath("$.message").value(MEMBER_NOT_FOUND.getMessage()))
            .andDo(print())
            .andDo(document("boards/upload-post-image/failure/member-not-found",
                getDocumentRequest(),
                getDocumentResponse(),
                pathParameters(parameterWithName("postId").description("이미지 수정할 게시글 Id")),
                requestHeaders(
                    headerWithName("Authorization").description("Access Token"))
            ));
	}

	@Test
	@DisplayName("게시물 이미지 업로드 실패 - 게시물 찾을 수 없는 경우")
	@WithMockUser
	void uploadPostImageFailure_PostNotFound() throws Exception {
		//given
		String email = "test@test.com";
		Principal principal = Mockito.mock(Principal.class);
		given(principal.getName()).willReturn(email);

		String filename = "test.png";
		String contentType = "image/png";
		MockMultipartFile file = new MockMultipartFile("image", filename, contentType,
			"test".getBytes());
		given(postService.uploadPostImage(anyString(), anyLong(), any()))
			.willThrow(new PostNotFoundException());

		//when
		//then
		mockMvc.perform(multipart("/api/boards/{postId}/images", 1)
				.file(file)
				.contentType(MediaType.MULTIPART_FORM_DATA)
				.header("Authorization", "accessToken"))
			.andExpect(status().is4xxClientError())
			.andExpect(jsonPath("$.code").value(POST_NOT_FOUND.getCode()))
			.andExpect(jsonPath("$.message").value(POST_NOT_FOUND.getMessage()))
			.andDo(print())
			.andDo(document("boards/upload-post-image/failure/post-not-found",
				getDocumentRequest(),
				getDocumentResponse(),
				pathParameters(parameterWithName("postId").description("이미지 수정할 게시글 Id")),
				requestHeaders(
					headerWithName("Authorization").description("Access Token"))
			));
	}

	@Test
	@DisplayName("게시물 이미지 업로드 실패 - 게시물 작성자가 아닐 경우")
	@WithMockUser
	void uploadPostImageFailure_NoPermissionUpdatePost() throws Exception {
		//given
		String email = "test@test.com";
		Principal principal = Mockito.mock(Principal.class);
		given(principal.getName()).willReturn(email);

		String filename = "test.png";
		String contentType = "image/png";
		MockMultipartFile file = new MockMultipartFile("image", filename, contentType,
			"test".getBytes());
		given(postService.uploadPostImage(anyString(), anyLong(), any()))
			.willThrow(new NoPermissionUpdatePostException());

		//when
		//then
		mockMvc.perform(multipart("/api/boards/{postId}/images", 1)
				.file(file)
				.contentType(MediaType.MULTIPART_FORM_DATA)
				.header("Authorization", "accessToken"))
			.andExpect(status().is4xxClientError())
			.andExpect(jsonPath("$.code").value(NO_PERMISSION_TO_UPDATE_POST.getCode()))
			.andExpect(jsonPath("$.message").value(NO_PERMISSION_TO_UPDATE_POST.getMessage()))
			.andDo(print())
			.andDo(document("boards/upload-post-image/failure/no-permission-update-post",
				getDocumentRequest(),
				getDocumentResponse(),
				pathParameters(parameterWithName("postId").description("이미지 수정할 게시글 Id")),
				requestHeaders(
					headerWithName("Authorization").description("Access Token"))
			));
	}

	@Test
	@DisplayName("게시물 이미지 업로드 성공")
	@WithMockUser
	void uploadPostImageSuccess() throws Exception {
		//given
		String email = "test@test.com";
		Principal principal = Mockito.mock(Principal.class);
		given(principal.getName()).willReturn(email);

		String filename = "test.png";
		String contentType = "image/png";
		MockMultipartFile file = new MockMultipartFile("image", filename, contentType,
			"test".getBytes());

		LocalDateTime date = LocalDateTime.of(2020, 12, 31, 23, 59, 59);

		PostImageUploadResponse response = PostImageUploadResponse.builder()
			.imageId(1L)
			.imageUrl("https://test")
			.representative(false)
			.createdAt(date)
			.build();
		given(postService.uploadPostImage(anyString(), anyLong(), any()))
			.willReturn(response);

		//when
		//then
		mockMvc.perform(multipart("/api/boards/{postId}/images", 1)
				.file(file)
				.contentType(MediaType.MULTIPART_FORM_DATA)
				.header("Authorization", "accessToken"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.imageId").value(response.getImageId()))
			.andExpect(jsonPath("$.imageUrl").value(response.getImageUrl()))
			.andExpect(jsonPath("$.isRepresentative").value(response.isRepresentative()))
			.andExpect(jsonPath("$.createdAt").value(response.getCreatedAt().toString()))
			.andDo(print())
			.andDo(document("boards/upload-post-image/success",
				getDocumentRequest(),
				getDocumentResponse(),
				pathParameters(parameterWithName("postId").description("이미지 수정할 게시글 Id")),
				requestHeaders(
					headerWithName("Authorization").description("Access Token"))
			));
	}

	@Test
	@DisplayName("게시물 이미지 삭제 실패 - 회원 찾을 수 없는 경우")
	@WithMockUser
	void deletePostImageFailure_MemberNotFound() throws Exception {
		//given
		String email = "test@test.com";
		Principal principal = Mockito.mock(Principal.class);
		given(principal.getName()).willReturn(email);

		given(postService.deletePostImage(anyString(), anyLong(), anyLong()))
			.willThrow(new MemberNotFoundException());

		//when
		//then
		mockMvc.perform(delete("/api/boards/{postId}/images/{imageId}", 1L, 1L)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "accessToken"))
			.andExpect(status().is4xxClientError())
			.andExpect(jsonPath("$.code").value(MEMBER_NOT_FOUND.getCode()))
			.andExpect(jsonPath("$.message").value(MEMBER_NOT_FOUND.getMessage()))
			.andDo(print())
			.andDo(document("boards/delete-post-image/failure/member-not-found",
				getDocumentRequest(),
				getDocumentResponse(),
				pathParameters(parameterWithName("postId").description("이미지 삭제할 게시글 Id"),
					parameterWithName("imageId").description("삭제할 이미지 Id")),
				requestHeaders(
					headerWithName("Authorization").description("Access Token"))
			));
	}

	@Test
	@DisplayName("게시물 이미지 삭제 실패 - 게시물 찾을 수 없는 경우")
	@WithMockUser
	void deletePostImageFailure_PostNotFound() throws Exception {
		//given
		String email = "test@test.com";
		Principal principal = Mockito.mock(Principal.class);
		given(principal.getName()).willReturn(email);

		given(postService.deletePostImage(anyString(), anyLong(), anyLong()))
			.willThrow(new PostNotFoundException());

		//when
		//then
		mockMvc.perform(delete("/api/boards/{postId}/images/{imageId}", 1L, 1L)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "accessToken"))
			.andExpect(status().is4xxClientError())
			.andExpect(jsonPath("$.code").value(POST_NOT_FOUND.getCode()))
			.andExpect(jsonPath("$.message").value(POST_NOT_FOUND.getMessage()))
			.andDo(print())
			.andDo(document("boards/delete-post-image/failure/post-not-found",
				getDocumentRequest(),
				getDocumentResponse(),
				pathParameters(parameterWithName("postId").description("이미지 삭제할 게시글 Id"),
					parameterWithName("imageId").description("삭제할 이미지 Id")),
				requestHeaders(
					headerWithName("Authorization").description("Access Token"))
			));
	}

	@Test
	@DisplayName("게시물 이미지 삭제 실패 - 게시물 작성자가 아닐 경우")
	@WithMockUser
	void deletePostImageFailure_NoPermissionUpdatePost() throws Exception {
		//given
		String email = "test@test.com";
		Principal principal = Mockito.mock(Principal.class);
		given(principal.getName()).willReturn(email);

		given(postService.deletePostImage(anyString(), anyLong(), anyLong()))
			.willThrow(new NoPermissionUpdatePostException());

		//when
		//then
		mockMvc.perform(delete("/api/boards/{postId}/images/{imageId}", 1L, 1L)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "accessToken"))
			.andExpect(status().is4xxClientError())
			.andExpect(jsonPath("$.code").value(NO_PERMISSION_TO_UPDATE_POST.getCode()))
			.andExpect(jsonPath("$.message").value(NO_PERMISSION_TO_UPDATE_POST.getMessage()))
			.andDo(print())
			.andDo(document("boards/delete-post-image/failure/no-permission-update-post",
				getDocumentRequest(),
				getDocumentResponse(),
				pathParameters(parameterWithName("postId").description("이미지 삭제할 게시글 Id"),
					parameterWithName("imageId").description("삭제할 이미지 Id")),
				requestHeaders(
					headerWithName("Authorization").description("Access Token"))
			));
	}

	@Test
	@DisplayName("게시물 이미지 삭제 성공")
	@WithMockUser
	void deletePostImageSuccess() throws Exception {
		//given
		String email = "test@test.com";
		Principal principal = Mockito.mock(Principal.class);
		given(principal.getName()).willReturn(email);

		given(postService.deletePostImage(anyString(), anyLong(), anyLong()))
			.willReturn(true);

		//when
		//then
		mockMvc.perform(delete("/api/boards/{postId}/images/{imageId}", 1L, 1L)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "accessToken"))
			.andExpect(status().isOk())
			.andDo(print())
			.andDo(document("boards/delete-post-image/success",
				getDocumentRequest(),
				getDocumentResponse(),
				pathParameters(parameterWithName("postId").description("이미지 삭제할 게시글 Id"),
					parameterWithName("imageId").description("삭제할 이미지 Id")),
				requestHeaders(
					headerWithName("Authorization").description("Access Token"))
			));
	}

	@Test
	@DisplayName("게시물 삭제 실패 - 회원 찾을 수 없는 경우")
	@WithMockUser
	void deletePostFailure_MemberNotFound() throws Exception {
		//given
		given(postService.deletePost(anyString(), anyLong()))
			.willThrow(new MemberNotFoundException());

		//when
		//then
		mockMvc.perform(delete("/api/boards/{postId}", 1)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "accessToken"))
			.andExpect(status().is4xxClientError())
			.andExpect(jsonPath("$.code").value(MEMBER_NOT_FOUND.getCode()))
			.andExpect(jsonPath("$.message").value(MEMBER_NOT_FOUND.getMessage()))
			.andDo(print())
			.andDo(document("boards/delete-post/failure/member-not-found",
				getDocumentRequest(),
				getDocumentResponse(),
				pathParameters(parameterWithName("postId").description("삭제할 게시글 Id")),
				requestHeaders(
					headerWithName("Authorization").description("Access Token"))
			));
	}

	@Test
	@DisplayName("게시물 삭제 실패 - 게시물 찾을 수 없는 경우")
	@WithMockUser
	void deletePostFailure_PostNotFound() throws Exception {
		//given
		given(postService.deletePost(anyString(), anyLong()))
			.willThrow(new PostNotFoundException());

		//when
		//then
		mockMvc.perform(delete("/api/boards/{postId}", 1)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "accessToken"))
			.andExpect(status().is4xxClientError())
			.andExpect(jsonPath("$.code").value(POST_NOT_FOUND.getCode()))
			.andExpect(jsonPath("$.message").value(POST_NOT_FOUND.getMessage()))
			.andDo(print())
			.andDo(document("boards/delete-post/failure/post-not-found",
				getDocumentRequest(),
				getDocumentResponse(),
				pathParameters(parameterWithName("postId").description("삭제할 게시글 Id")),
				requestHeaders(
					headerWithName("Authorization").description("Access Token"))
			));
	}

	@Test
	@DisplayName("게시물 삭제 실패 - 게시물 작성자가 아닐 경우")
	@WithMockUser
	void deletePostFailure_NoPermissionDeletePost() throws Exception {
		//given
		given(postService.deletePost(anyString(), anyLong()))
			.willThrow(new NoPermissionDeletePostException());

		//when
		//then
		mockMvc.perform(delete("/api/boards/{postId}", 1)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "accessToken"))
			.andExpect(status().is4xxClientError())
			.andExpect(jsonPath("$.code").value(NO_PERMISSION_TO_DELETE_POST.getCode()))
			.andExpect(jsonPath("$.message").value(NO_PERMISSION_TO_DELETE_POST.getMessage()))
			.andDo(print())
			.andDo(document("boards/delete-post/failure/no-permission-update-post",
				getDocumentRequest(),
				getDocumentResponse(),
				pathParameters(parameterWithName("postId").description("삭제할 게시글 Id")),
				requestHeaders(
					headerWithName("Authorization").description("Access Token"))
			));
	}

	@Test
	@DisplayName("게시물 삭제 성공")
	@WithMockUser
	void deletePostSuccess() throws Exception {
		//given
		given(postService.deletePost(anyString(), anyLong()))
			.willReturn(true);

		//then
		mockMvc.perform(delete("/api/boards/{postId}", 1)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "accessToken"))
			.andExpect(status().isOk())
			.andDo(print())
			.andDo(document("boards/delete-post/success",
				getDocumentRequest(),
				getDocumentResponse(),
				pathParameters(parameterWithName("postId").description("삭제할 게시글 Id")),
				requestHeaders(
					headerWithName("Authorization").description("Access Token"))
			));
	}


	@Test
	@DisplayName("게시물 조회 성공")
	@WithMockUser
	void getPosts_Success() throws Exception {

		LocalDateTime date1 = LocalDateTime.of(2022, 12, 31, 15, 59, 59);
		LocalDateTime date2 = LocalDateTime.of(2022, 12, 31, 17, 59, 59);
		LocalDateTime date3 = LocalDateTime.of(2022, 12, 31, 19, 59, 59);
		LocalDateTime date4 = LocalDateTime.of(2022, 12, 31, 21, 59, 59);
		LocalDateTime date5 = LocalDateTime.of(2022, 12, 31, 23, 59, 59);

		PostDetailResponse post1 = PostDetailResponse.builder()
				.postId(1L)
				.title("제목입니다1")
				.content("내용입니다1")
				.imageUrl("http://testImage1")
				.createdAt(date1)
				.commentCnt(5)
				.heartCnt(10)
				.build();

		PostDetailResponse post2 = PostDetailResponse.builder()
				.postId(2L)
				.title("제목입니다2")
				.content("내용입니다2")
				.imageUrl("http://testImage2")
				.createdAt(date2)
				.commentCnt(2)
				.heartCnt(0)
				.build();

		PostDetailResponse post3 = PostDetailResponse.builder()
				.postId(3L)
				.title("제목입니다3")
				.content("내용입니다3")
				.createdAt(date3)
				.commentCnt(7)
				.heartCnt(2)
				.build();

		PostDetailResponse post4 = PostDetailResponse.builder()
				.postId(4L)
				.title("제목입니다4")
				.content("내용입니다4")
				.imageUrl("http://testImage4")
				.createdAt(date4)
				.commentCnt(3)
				.heartCnt(10)
				.build();

		PostDetailResponse post5 = PostDetailResponse.builder()
				.postId(5L)
				.title("제목입니다5")
				.content("내용입니다5")
				.createdAt(date5)
				.commentCnt(8)
				.heartCnt(3)
				.build();

		List<PostDetailResponse> postResponses = List.of(post5, post4, post3, post2, post1);

		PostsGetResponse response = PostsGetResponse.builder()
				.posts(postResponses)
				.totalPostsCnt(5)
				.nextCursor(-1)
				.build();

		given(postService.getPosts(anyLong(), anyInt(), anyLong()))
				.willReturn(response);

		mockMvc.perform((get("/api/boards/{categoryId}/list", 1L)
				.param("size", "5")
				.param("lastPostId", "9223372036854775807")
				.header("Authorization", "accessToken")))
				.andExpect(jsonPath("$.posts[0].postId").value(post5.getPostId()))
				.andExpect(jsonPath("$.posts[0].title").value(post5.getTitle()))
				.andExpect(jsonPath("$.posts[0].content").value(post5.getContent()))
				.andExpect(jsonPath("$.posts[0].imageUrl").value(post5.getImageUrl()))
				.andExpect(jsonPath("$.posts[0].createdAt").value(post5.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))))
				.andExpect(jsonPath("$.posts[0].commentCnt").value(post5.getCommentCnt()))
				.andExpect(jsonPath("$.posts[0].heartCnt").value(post5.getHeartCnt()))
				.andExpect(status().isOk())
				.andDo(print())
				.andDo(document("boards/get-posts/success",
						getDocumentRequest(),
						getDocumentResponse(),
						pathParameters(parameterWithName("categoryId").description("카테고리 id별 게시물 목록 조회")),
						requestParameters(parameterWithName("size").description("한 번에 보여줄 게시물의 개수"),
								parameterWithName("lastPostId").description("한 번에 읽은 게시물들의 가장 마지막 게시물 Id - 처음 조회할 경우 Long 타입의 최대값")),
						requestHeaders(
								headerWithName("Authorization").description("Access Token"))
				));

	}

	@Test
	@DisplayName("게시물 조회 실패 - 카테고리 없음")
	@WithMockUser
	void getPosts_Fail_NoCategory () throws Exception {

		given(postService.getPosts(anyLong(), anyInt(), anyLong()))
				.willThrow(new CategoryNotFoundException());

		mockMvc.perform((get("/api/boards/{categoryId}/list", 1L)
						.param("size", "5")
						.param("lastPostId", "9223372036854775807")
						.header("Authorization", "accessToken")))
				.andExpect(status().is4xxClientError())
				.andExpect(jsonPath("$.code").value(CATEGORY_NOT_FOUND.getCode()))
				.andExpect(jsonPath("$.message").value(CATEGORY_NOT_FOUND.getMessage()))
				.andDo(print())
				.andDo(document("boards/get-posts/failure/category-not-found",
						getDocumentRequest(),
						getDocumentResponse(),
						pathParameters(parameterWithName("categoryId").description("카테고리 id별 게시물 목록 조회")),
						requestParameters(parameterWithName("size").description("한 번에 보여줄 게시물의 개수"),
								parameterWithName("lastPostId").description("한 번에 읽은 게시물들의 가장 마지막 게시물 Id - 처음 조회할 경우 Long 타입의 최대값")),
						requestHeaders(
								headerWithName("Authorization").description("Access Token"))
				));
	}
}