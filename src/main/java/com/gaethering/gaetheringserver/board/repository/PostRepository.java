package com.gaethering.gaetheringserver.board.repository;

import com.gaethering.gaetheringserver.board.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}
