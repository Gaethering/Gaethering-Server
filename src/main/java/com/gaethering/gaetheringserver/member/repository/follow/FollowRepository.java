package com.gaethering.gaetheringserver.member.repository.follow;

import com.gaethering.gaetheringserver.member.domain.Follow;
import com.gaethering.gaetheringserver.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    Long countByFollowee(Member member);

    Long countByFollower(Member member);
}
