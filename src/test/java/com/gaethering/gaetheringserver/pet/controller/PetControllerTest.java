package com.gaethering.gaetheringserver.pet.controller;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gaethering.gaetheringserver.member.type.Gender;
import com.gaethering.gaetheringserver.pet.dto.PetImageUpdateResponse;
import com.gaethering.gaetheringserver.pet.dto.PetProfileResponse;
import com.gaethering.gaetheringserver.pet.dto.PetProfileUpdateRequest;
import com.gaethering.gaetheringserver.pet.service.PetService;
import java.security.Principal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@WebMvcTest(PetController.class)
@ActiveProfiles("test")
class PetControllerTest {

    @MockBean
    private PetService petService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser
    public void setRepresentativePet() throws Exception {
        //given
        String email = "test@test.com";
        Principal principal = Mockito.mock(Principal.class);
        given(principal.getName()).willReturn(email);

        //when
        //then
        mockMvc.perform(patch("/api/mypage/pets/1/representative")
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf()))
            .andDo(print())
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void updatePetImage() throws Exception {
        // given
        String filename = "test.png";
        String contentType = "image/png";

        MockMultipartFile image = new MockMultipartFile("image", filename, contentType,
            "test".getBytes());
        PetImageUpdateResponse response = PetImageUpdateResponse.builder()
            .imageUrl("https://test").build();

        given(petService.updatePetImage(1L, image))
            .willReturn(PetImageUpdateResponse.builder().imageUrl(response.getImageUrl()).build());

        // when
        // then
        MockMultipartHttpServletRequestBuilder builder =
            MockMvcRequestBuilders.multipart("/api/mypage/pets/1/image");

        builder.with(request -> {
            request.setMethod("PATCH");
            return request;
        });

        mockMvc.perform(
                builder
                    .file(image)
                    .with(csrf())
            ).andExpect(jsonPath("$.imageUrl").value(response.getImageUrl()))
            .andDo(print())
            .andExpect(status().isOk());
    }
    @Test
    @WithMockUser
    public void getPetProfile() throws Exception {
        //given
        PetProfileResponse petProfile = PetProfileResponse.builder().name("해")
            .birth(LocalDate.parse("2021-12-01")).gender(
                Gender.valueOf("FEMALE")).breed("말티즈").weight(3.6f).isNeutered(true)
            .description("하얘요").imageUrl("https://test").build();
        given(petService.getPetProfile(anyLong())).willReturn(petProfile);

        //when
        //then
        mockMvc.perform(get("/api/pets/1/profile").contentType(MediaType.APPLICATION_JSON)
                .with(csrf()))
            .andExpect(jsonPath("$.name").value(petProfile.getName()))
            .andExpect(jsonPath("$.birth").value(String.valueOf(petProfile.getBirth())))
            .andExpect(jsonPath("$.gender").value(String.valueOf(petProfile.getGender())))
            .andExpect(jsonPath("$.breed").value(petProfile.getBreed()))
            .andExpect(jsonPath("$.weight").value(petProfile.getWeight()))
            .andExpect(jsonPath("$.description").value(petProfile.getDescription()))
            .andExpect(jsonPath("$.imageUrl").value(petProfile.getImageUrl()))
            .andDo(print()).andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    public void updatePetProfile() throws Exception {
        //given
        PetProfileUpdateRequest request = PetProfileUpdateRequest.builder()
            .weight(3.5f)
            .isNeutered(true)
            .description("귀여워요")
            .build();
        PetProfileResponse response = PetProfileResponse.builder()
            .name("해")
            .birth(LocalDate.parse("2021-12-01"))
            .gender(Gender.valueOf("FEMALE"))
            .breed("말티즈")
            .weight(3.5f)
            .isNeutered(true)
            .description("귀여워요")
            .imageUrl("https://test")
            .build();
        given(petService.updatePetProfile(1L, request.getWeight(), request.isNeutered(), request.getDescription()))
            .willReturn(response);

        String requestString = objectMapper.writeValueAsString(request);

        //when
        //then
        mockMvc.perform(patch("/api/mypage/pets/1")
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .content(requestString))
            .andDo(print())
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    public void deletePetProfile() throws Exception {
        //given
        String email = "test@test.com";
        Principal principal = Mockito.mock(Principal.class);
        given(principal.getName()).willReturn(email);

        given(petService.deletePetProfile(anyString(), anyLong()))
            .willReturn(true);

        //when
        //then
        mockMvc.perform(delete("/api/mypage/pets/1")
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf()))
            .andDo(print())
            .andExpect(status().isOk());
    }
}