package com.gaethering.gaetheringserver.pet.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.gaethering.gaetheringserver.config.SecurityConfig;
import com.gaethering.gaetheringserver.filter.JwtAuthenticationFilter;
import com.gaethering.gaetheringserver.pet.service.PetService;
import java.security.Principal;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = PetController.class, excludeFilters = {
    @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
        classes = {SecurityConfig.class, JwtAuthenticationFilter.class})
})
@ActiveProfiles("test")
class PetControllerTest {

    @MockBean
    private PetService petService;

    @Autowired
    private MockMvc mockMvc;

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
}