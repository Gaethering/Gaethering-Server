package com.gaethering.gaetheringserver.member.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.gaethering.gaetheringserver.config.JpaConfig;
import com.gaethering.gaetheringserver.config.QuerydslConfig;
import com.gaethering.gaetheringserver.member.domain.Member;
import com.gaethering.gaetheringserver.member.domain.MemberProfile;
import com.gaethering.gaetheringserver.pet.domain.Pet;
import com.gaethering.gaetheringserver.member.repository.member.MemberProfileRepository;
import com.gaethering.gaetheringserver.member.repository.member.MemberRepository;
import com.gaethering.gaetheringserver.pet.repository.PetRepository;
import com.gaethering.gaetheringserver.member.type.Gender;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

@DataJpaTest
@Import({JpaConfig.class, QuerydslConfig.class})
@Transactional
public class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private MemberProfileRepository memberProfileRepository;
    @Autowired
    private PetRepository petRepository;
    @Autowired
    private EntityManager em;

    private List<Pet> pets;
    private MemberProfile memberProfile;
    private Member member;

    @BeforeEach
    public void setUP() {
        Pet pet1 = Pet.builder()
            .name("pet1")
            .isRepresentative(true)
            .build();
        Pet pet2 = Pet.builder()
            .name("pet2")
            .isRepresentative(false)
            .build();
        pets = List.of(pet1, pet2);
        memberProfile = MemberProfile.builder()
            .gender(Gender.MALE)
            .phoneNumber("010-0000-0000")
            .mannerDegree(36.5f)
            .build();
        member = Member.builder()
            .email("member1@test.com")
            .memberProfile(memberProfile)
            .pets(new ArrayList<>())
            .build();
        for (Pet pet : pets) {
            member.addPet(pet);
        }
        petRepository.saveAll(pets);
        memberProfileRepository.save(memberProfile);
        member = memberRepository.save(member);
        em.flush();
        em.clear();
    }

    @Test
    public void findByEmailFailure() {
        //given
        String email = "wrongEmail";

        //when
        Optional<Member> optionalMember = memberRepository.findByEmail(email);

        //then
        assertThat(optionalMember.isPresent()).isFalse();
    }

    @Test
    public void findByEmailSuccess() {
        //given
        String email = member.getEmail();

        //when
        Optional<Member> optionalMember = memberRepository.findByEmail(email);

        //then
        assertThat(optionalMember.isPresent()).isTrue();
        Member testMember = optionalMember.get();
        assertThat(testMember.getEmail()).isEqualTo(member.getEmail());
        assertMemberProfile(testMember);
        assertPets(testMember);
    }

    @Test
    public void findByIdFailure() {
        //given
        Long id = Long.MAX_VALUE;

        //when
        Optional<Member> optionalMember = memberRepository.findById(id);

        //then
        assertThat(optionalMember.isPresent()).isFalse();
    }

    @Test
    public void findByIdSuccess() {
        //given
        Long id = member.getId();

        //when
        Optional<Member> optionalMember = memberRepository.findById(id);

        //then
        assertThat(optionalMember.isPresent()).isTrue();
        Member testMember = optionalMember.get();
        assertThat(testMember.getEmail()).isEqualTo(member.getEmail());
        assertMemberProfile(testMember);
        assertPets(testMember);
    }

    private void assertMemberProfile(Member testMember) {
        assertThat(testMember.getMemberProfile().getPhoneNumber()).isEqualTo(
            memberProfile.getPhoneNumber());
        assertThat(testMember.getMemberProfile().getMannerDegree()).isEqualTo(
            memberProfile.getMannerDegree());
        assertThat(testMember.getMemberProfile().getGender()).isEqualTo(
            memberProfile.getGender());
    }

    private void assertPets(Member testMember) {
        for (int i = 0; i < pets.size(); i++) {
            assertThat(testMember.getPets().get(i).getName()).isEqualTo(
                pets.get(i).getName());
        }
    }
}
