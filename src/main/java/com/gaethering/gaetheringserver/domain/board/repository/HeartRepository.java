package com.gaethering.gaetheringserver.domain.board.repository;

import com.gaethering.gaetheringserver.domain.board.entity.Heart;
import com.gaethering.gaetheringserver.domain.board.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface HeartRepository extends JpaRepository<Heart, Long> {

	Long countByPost(Post post);

	@Modifying
	@Query("delete from Heart h where h.post = :post")
	void deleteHeartAllByPostId(@Param("post") Post post);
}