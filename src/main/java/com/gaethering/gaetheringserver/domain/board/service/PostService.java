package com.gaethering.gaetheringserver.domain.board.service;

import com.gaethering.gaetheringserver.domain.board.dto.PostWriteRequest;
import com.gaethering.gaetheringserver.domain.board.dto.PostWriteResponse;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface PostService {

    PostWriteResponse writePost(String email, List<MultipartFile> files, PostWriteRequest request);

}