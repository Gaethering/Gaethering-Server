package com.gaethering.gaetheringserver.domain.member.repository.member;

import com.gaethering.gaetheringserver.domain.member.entity.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MemberRepository extends JpaRepository<Member, Long>, CustomMemberRepository {

    boolean existsByEmail(String email);

	@Query("select m from Member m where m.email = :email")
	Optional<Member> findMembersByEmail(@Param("email")String email);
}
