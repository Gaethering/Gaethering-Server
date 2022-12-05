package com.gaethering.gaetheringserver.member.repository.member;

import com.gaethering.gaetheringserver.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long>, CustomMemberRepository {

    boolean existsByEmail(String email);
}
