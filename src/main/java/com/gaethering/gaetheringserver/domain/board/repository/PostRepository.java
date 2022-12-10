package com.gaethering.gaetheringserver.domain.board.repository;

import com.gaethering.gaetheringserver.domain.board.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {

}