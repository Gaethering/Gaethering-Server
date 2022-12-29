package com.gaethering.gaetheringserver.domain.pet.repository;

import com.gaethering.gaetheringserver.domain.pet.entity.Pet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PetRepository extends JpaRepository<Pet, Long> {

}
