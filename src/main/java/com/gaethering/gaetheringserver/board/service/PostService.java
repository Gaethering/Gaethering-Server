package com.gaethering.gaetheringserver.board.service;

import com.gaethering.gaetheringserver.board.dto.PostRequest;
import com.gaethering.gaetheringserver.board.dto.PostResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PostService {

    PostResponse writePost(String email, List<MultipartFile> files, PostRequest request);

    List<String> getImageUrlInRequest(List<MultipartFile> files);
}