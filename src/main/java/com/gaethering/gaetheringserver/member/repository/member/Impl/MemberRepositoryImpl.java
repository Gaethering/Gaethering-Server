package com.gaethering.gaetheringserver.member.repository.member.Impl;

import static com.gaethering.gaetheringserver.member.domain.QMember.member;
import static com.gaethering.gaetheringserver.member.domain.QMemberProfile.memberProfile;
import static com.gaethering.gaetheringserver.pet.domain.QPet.pet;

import com.gaethering.gaetheringserver.core.repository.support.Querydsl4RepositorySupport;
import com.gaethering.gaetheringserver.member.domain.Member;
import com.gaethering.gaetheringserver.member.repository.member.CustomMemberRepository;
import com.querydsl.core.types.dsl.BooleanExpression;
import java.util.Optional;

public class MemberRepositoryImpl extends Querydsl4RepositorySupport implements
    CustomMemberRepository {

    public MemberRepositoryImpl() {
        super(Member.class);
    }

    @Override
    public Optional<Member> findByEmail(String email) {
        return Optional.ofNullable(
            selectFrom(member)
                .join(member.pets, pet).fetchJoin()
                .join(member.memberProfile, memberProfile).fetchJoin()
                .where(emailEqual(email))
                .fetchOne());
    }

    private static BooleanExpression emailEqual(String email) {
        return member.email.eq(email);
    }
}
