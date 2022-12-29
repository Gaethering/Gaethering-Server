package com.gaethering.gaetheringserver.domain.board.service;

import com.gaethering.gaetheringserver.domain.board.dto.HeartResponse;

public interface HeartService {

    HeartResponse pushHeart (Long postId, String email);
}
