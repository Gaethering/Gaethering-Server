package com.gaethering.gaetheringserver.domain.board.service;

import com.gaethering.gaetheringserver.domain.board.dto.PostImageUploadResponse;
import com.gaethering.gaetheringserver.domain.board.dto.PostWriteRequest;
import com.gaethering.gaetheringserver.domain.board.dto.PostWriteResponse;
import com.gaethering.gaetheringserver.domain.board.dto.PostUpdateRequest;
import com.gaethering.gaetheringserver.domain.board.dto.PostUpdateResponse;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface PostService {

    PostWriteResponse writePost(String email, Long categoryId, List<MultipartFile> files, PostWriteRequest request);

    PostUpdateResponse updatePost(String email, Long postId, PostUpdateRequest request);

	PostImageUploadResponse uploadPostImage(String email, Long postId, MultipartFile file);

	boolean deletePostImage(String email, Long postId, Long imageId);

	boolean deletePost(String email, Long postId);
}