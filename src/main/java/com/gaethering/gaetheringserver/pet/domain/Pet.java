package com.gaethering.gaetheringserver.pet.domain;

import com.gaethering.gaetheringserver.core.type.Gender;
import com.gaethering.gaetheringserver.member.domain.Member;
import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
public class Pet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pet_id")
    private Long id;

    private String name;

    private LocalDate birth;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private String breed;

    private float weight;

    private boolean isNeutered;

    private boolean isRepresentative;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    public void setMember(Member member) {
        this.member = member;
    }

    public void setRepresentative(boolean representative) {
        isRepresentative = representative;
    }

    public void updateImage(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void updatePetProfile(float weight, boolean isNeutered, String description) {
        this.weight = weight;
        this.isNeutered = isNeutered;
        this.description = description;
    }
}