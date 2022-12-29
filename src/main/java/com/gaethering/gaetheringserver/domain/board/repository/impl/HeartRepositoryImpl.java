package com.gaethering.gaetheringserver.domain.board.repository.impl;

import static com.gaethering.gaetheringserver.domain.board.entity.QHeart.heart;

import com.gaethering.gaetheringserver.core.repository.support.Querydsl4RepositorySupport;
import com.gaethering.gaetheringserver.domain.board.entity.Heart;
import com.gaethering.gaetheringserver.domain.board.repository.CustomHeartRepository;

public class HeartRepositoryImpl extends Querydsl4RepositorySupport implements
	CustomHeartRepository {

	public HeartRepositoryImpl() {
		super(Heart.class);
	}

	@Override
	public long deleteHeartAllByPostId(Long id) {
		return getQueryFactory()
			.delete(heart)
			.where(heart.post.id.eq(id))
			.execute();
	}
}
