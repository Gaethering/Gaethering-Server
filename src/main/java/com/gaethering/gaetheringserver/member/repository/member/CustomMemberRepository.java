package com.gaethering.gaetheringserver.member.repository.member;


import com.gaethering.gaetheringserver.member.domain.Member;
import java.util.Optional;

public interface CustomMemberRepository {
    Optional<Member> findByEmail(String email);
}
