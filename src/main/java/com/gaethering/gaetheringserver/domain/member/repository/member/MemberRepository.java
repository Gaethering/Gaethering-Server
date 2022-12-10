package com.gaethering.gaetheringserver.domain.member.repository.member;

import com.gaethering.gaetheringserver.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long>, CustomMemberRepository {

    boolean existsByEmail(String email);
}
