package com.gaethering.gaetheringserver.domain.board.repository;

import com.gaethering.gaetheringserver.domain.board.entity.Heart;
import com.gaethering.gaetheringserver.domain.board.entity.Post;
import com.gaethering.gaetheringserver.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface HeartRepository extends JpaRepository<Heart, Long>, CustomHeartRepository {

	Long countByPost(Post post);

	Optional<Heart> findByPostAndMember (Post post, Member member);

	boolean existsByPostAndMember (Post post, Member member);

	long deleteHeartAllByPostId(Long id);
}