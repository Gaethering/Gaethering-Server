package com.gaethering.gaetheringserver.member.repository.pet;

import com.gaethering.gaetheringserver.member.domain.Pet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PetRepository extends JpaRepository<Pet, Long> {

}
