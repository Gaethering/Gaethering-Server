package com.gaethering.gaetheringserver.member.domain;

import com.gaethering.gaetheringserver.core.domain.BaseTimeEntity;
import com.gaethering.gaetheringserver.member.type.MemberRole;
import com.gaethering.gaetheringserver.member.type.MemberStatus;
import com.gaethering.gaetheringserver.pet.domain.Pet;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
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
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(unique = true)
    private String email;

    private String name;

    private LocalDate birth;

    private String nickname;

    private String password;

    @Enumerated(EnumType.STRING)
    private MemberStatus status;

    private LocalDateTime accessDate;

    private boolean isEmailAuth;

    @Enumerated(EnumType.STRING)
    private MemberRole role;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "member_profile_id")
    private MemberProfile memberProfile;

    @OneToMany(mappedBy = "member")
    private List<Pet> pets = new ArrayList<>();

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setPets(List<Pet> pets) {
        this.pets = pets;
    }

    public void addPet(Pet pet) {
        pet.setMember(this);
        pets.add(pet);
    }

}