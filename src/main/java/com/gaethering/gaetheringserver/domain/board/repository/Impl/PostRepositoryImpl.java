package com.gaethering.gaetheringserver.domain.board.repository.Impl;

import com.gaethering.gaetheringserver.core.repository.support.Querydsl4RepositorySupport;
import com.gaethering.gaetheringserver.domain.board.entity.Post;
import com.gaethering.gaetheringserver.domain.board.repository.CustomPostRepository;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import static com.gaethering.gaetheringserver.domain.board.entity.QPost.post;


@Repository
@Transactional
public class PostRepositoryImpl extends Querydsl4RepositorySupport implements CustomPostRepository {

    public PostRepositoryImpl() {
        super(Post.class);
    }

    @Override
    public long updateViewCountByPostId(Long postId) {

        return getQueryFactory().update(post)
                .set(post.viewCnt, post.viewCnt.add(1))
                .where(postIdEqual(postId))
                .execute();
    }

    private static BooleanExpression postIdEqual(Long postId) {
        return post.id.eq(postId);
    }
}