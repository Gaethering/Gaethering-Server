package com.gaethering.gaetheringserver.domain.board.repository;

import com.gaethering.gaetheringserver.domain.board.entity.Comment;
import com.gaethering.gaetheringserver.domain.board.entity.Post;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

	@Modifying
	@Query("delete from Comment c where c.post = :post")
	void deleteCommentsAllByPostId(@Param("post") Post post);

	List<Comment> findAllByPostAndIdIsLessThanOrderByIdDesc(Post post, Long lastCommentId, PageRequest pageRequest);

	long countByPost(Post post);
}
