package com.gaethering.gaetheringserver.member.repository.member;

import com.gaethering.gaetheringserver.member.domain.MemberProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberProfileRepository extends JpaRepository<MemberProfile, Long> {

}
