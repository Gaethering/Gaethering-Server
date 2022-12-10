package com.gaethering.gaetheringserver.domain.board.repository;

import com.gaethering.gaetheringserver.domain.board.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {

}
