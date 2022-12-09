package com.gaethering.gaetheringserver.board.repository;

import com.gaethering.gaetheringserver.board.domain.PostImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostImageRepository extends JpaRepository<PostImage, Long> {
}