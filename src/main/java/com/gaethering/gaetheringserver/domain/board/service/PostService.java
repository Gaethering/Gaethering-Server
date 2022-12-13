package com.gaethering.gaetheringserver.domain.board.service;

import com.gaethering.gaetheringserver.domain.board.dto.PostImageUpdateResponse;
import com.gaethering.gaetheringserver.domain.board.dto.PostRequest;
import com.gaethering.gaetheringserver.domain.board.dto.PostResponse;
import com.gaethering.gaetheringserver.domain.board.dto.PostUpdateRequest;
import com.gaethering.gaetheringserver.domain.board.dto.PostUpdateResponse;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface PostService {

    PostResponse writePost(String email, List<MultipartFile> files, PostRequest request);

    PostUpdateResponse updatePost(String email, Long postId, PostUpdateRequest request);

	PostImageUpdateResponse uploadPostImage(String email, Long postId, MultipartFile file);
}