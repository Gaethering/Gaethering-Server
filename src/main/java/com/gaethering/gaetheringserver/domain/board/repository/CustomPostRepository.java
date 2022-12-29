package com.gaethering.gaetheringserver.domain.board.repository;

public interface CustomPostRepository {

    long updateViewCountByPostId(Long postId);
}