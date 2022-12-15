package com.gaethering.gaetheringserver.domain.board.controller;

import com.gaethering.gaetheringserver.domain.board.dto.PostImageUploadResponse;
import com.gaethering.gaetheringserver.domain.board.dto.PostWriteRequest;
import com.gaethering.gaetheringserver.domain.board.dto.PostWriteResponse;
import com.gaethering.gaetheringserver.domain.board.dto.PostUpdateRequest;
import com.gaethering.gaetheringserver.domain.board.dto.PostUpdateResponse;
import com.gaethering.gaetheringserver.domain.board.service.PostService;
import java.security.Principal;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("${api-prefix}")
@RequiredArgsConstructor
public class PostController {

	private final PostService postService;

	@PostMapping(value = "/boards")
	public ResponseEntity<PostWriteResponse> writePost
		(@RequestPart(value = "data") @Valid PostWriteRequest request,
			@RequestPart(value = "images", required = false) List<MultipartFile> files,
			Principal principal) {

		String email = principal.getName();
		PostWriteResponse response = postService.writePost(email, files, request);

		return ResponseEntity.status(HttpStatus.CREATED)
			.contentType(MediaType.APPLICATION_JSON).body(response);
	}

	@PatchMapping("/boards/{postId}")
	public ResponseEntity<PostUpdateResponse> updatePost(@PathVariable Long postId,
		@RequestBody PostUpdateRequest request, Principal principal) {

		return ResponseEntity.ok(postService.updatePost(principal.getName(), postId, request));
	}

	@PostMapping("/boards/{postId}/images")
	public ResponseEntity<PostImageUploadResponse> uploadPostImage(@PathVariable Long postId,
		@RequestPart(value = "image", required = false) MultipartFile file,
		Principal principal) {

		return ResponseEntity.ok(
			postService.uploadPostImage(principal.getName(), postId, file));
	}

	@DeleteMapping("/boards/{postId}/images/{imageId}")
	public ResponseEntity<Void> deletePostImage(@PathVariable Long postId,
		@PathVariable Long imageId,
		Principal principal) {

		postService.deletePostImage(principal.getName(), postId, imageId);

		return ResponseEntity.ok().build();
	}
}