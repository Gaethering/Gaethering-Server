package com.gaethering.gaetheringserver.domain.member.repository.member;

import com.gaethering.gaetheringserver.domain.member.entity.MemberProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberProfileRepository extends JpaRepository<MemberProfile, Long> {

}
