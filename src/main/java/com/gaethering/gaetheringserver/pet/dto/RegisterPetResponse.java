package com.gaethering.gaetheringserver.pet.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class RegisterPetResponse {

    private String petName;

}
