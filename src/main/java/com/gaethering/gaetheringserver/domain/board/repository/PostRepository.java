package com.gaethering.gaetheringserver.domain.board.repository;

import com.gaethering.gaetheringserver.domain.board.entity.Category;
import com.gaethering.gaetheringserver.domain.board.entity.Post;
import com.gaethering.gaetheringserver.domain.member.entity.Member;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long>, CustomPostRepository {

    List<Post> findAllByMember(Member member);

    List<Post> findAllByCategoryAndIdIsLessThanOrderByIdDesc(Category category, long lastPostId, PageRequest pageRequest);

    long countByCategory (Category category);
}