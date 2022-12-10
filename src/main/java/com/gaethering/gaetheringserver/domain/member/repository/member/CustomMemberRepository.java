package com.gaethering.gaetheringserver.domain.member.repository.member;


import com.gaethering.gaetheringserver.domain.member.entity.Member;
import java.util.Optional;

public interface CustomMemberRepository {

    Optional<Member> findByEmail(String email);
}
