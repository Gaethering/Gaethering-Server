package com.gaethering.gaetheringserver.domain.board.repository;

import com.gaethering.gaetheringserver.domain.board.entity.Post;
import com.gaethering.gaetheringserver.domain.board.entity.PostImage;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PostImageRepository extends JpaRepository<PostImage, Long> {

	List<PostImage> findAllByPost(Post post);

	Optional<PostImage> findByPostAndIsRepresentativeIsTrue (Post post);
}