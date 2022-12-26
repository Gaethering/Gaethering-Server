package com.gaethering.gaetheringserver.domain.board.repository;

import com.gaethering.gaetheringserver.domain.board.entity.Comment;
import com.gaethering.gaetheringserver.domain.board.entity.Post;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long>, CustomCommentRepository {

	long deleteCommentsAllByPostId(Long id);

	List<Comment> findAllByPostAndIdIsLessThanOrderByIdDesc(Post post, Long lastCommentId, PageRequest pageRequest);

	long countByPost(Post post);
}
