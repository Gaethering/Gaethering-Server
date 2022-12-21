package com.gaethering.gaetheringserver.domain.chat.repository;

import com.gaethering.gaetheringserver.domain.chat.entity.WalkingTime;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WalkingTimeRepository extends JpaRepository<WalkingTime, Long> {

}
