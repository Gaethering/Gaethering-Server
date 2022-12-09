package com.gaethering.gaetheringserver.board.repository;

import com.gaethering.gaetheringserver.board.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
