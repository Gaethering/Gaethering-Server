package com.gaethering.gaetheringserver.domain.member.repository.follow;

import com.gaethering.gaetheringserver.domain.member.entity.Follow;
import com.gaethering.gaetheringserver.domain.member.entity.Member;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    Long countByFollowee(Member member);

    Long countByFollower(Member member);

    List<Follow> findByFollowee(Member member);

    List<Follow> findByFollower(Member member);

    Integer removeByFollowerAndFollowee(Member follower, Member followee);
}
