package com.gaethering.gaetheringserver.pet.repository;

import com.gaethering.gaetheringserver.pet.domain.Pet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PetRepository extends JpaRepository<Pet, Long> {

}
