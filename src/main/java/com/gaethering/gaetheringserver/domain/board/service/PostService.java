package com.gaethering.gaetheringserver.domain.board.service;

import com.gaethering.gaetheringserver.domain.board.dto.PostRequest;
import com.gaethering.gaetheringserver.domain.board.dto.PostResponse;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface PostService {

    PostResponse writePost(String email, List<MultipartFile> files, PostRequest request);

}