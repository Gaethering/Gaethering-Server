package com.gaethering.gaetheringserver.domain.board.repository.impl;

import static com.gaethering.gaetheringserver.domain.board.entity.QComment.comment;

import com.gaethering.gaetheringserver.core.repository.support.Querydsl4RepositorySupport;
import com.gaethering.gaetheringserver.domain.board.entity.Comment;
import com.gaethering.gaetheringserver.domain.board.repository.CustomCommentRepository;

public class CommentRepositoryImpl extends Querydsl4RepositorySupport implements
	CustomCommentRepository {

	public CommentRepositoryImpl() {
		super(Comment.class);
	}

	@Override
	public long deleteCommentsAllByPostId(Long id) {
		return getQueryFactory()
			.delete(comment)
			.where(comment.post.id.eq(id))
			.execute();
	}
}
