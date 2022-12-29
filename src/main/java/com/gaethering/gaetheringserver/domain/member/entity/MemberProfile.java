package com.gaethering.gaetheringserver.domain.member.entity;

import com.gaethering.gaetheringserver.core.type.Gender;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_profile_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private String phoneNumber;

    private float mannerDegree;

    @PrePersist
    public void initMannerDegree() {
        this.mannerDegree = 36.5f;
    }

}
