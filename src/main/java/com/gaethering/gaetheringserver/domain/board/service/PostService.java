package com.gaethering.gaetheringserver.domain.board.service;

import com.gaethering.gaetheringserver.domain.board.dto.*;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface PostService {

    PostWriteResponse writePost(String email, List<MultipartFile> files, PostWriteRequest request);

    PostUpdateResponse updatePost(String email, Long postId, PostUpdateRequest request);

	PostImageUploadResponse uploadPostImage(String email, Long postId, MultipartFile file);

	boolean deletePostImage(String email, Long postId, Long imageId);

	boolean deletePost(String email, Long postId);

	PostsGetResponse getPosts (String email, Long categoryId, int size, long lastCommentId);
}