package com.gaethering.gaetheringserver.member.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.gaethering.gaetheringserver.member.service.MemberService;
import java.security.Principal;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(MemberController.class)
@ActiveProfiles("test")
class MemberControllerTest {

    @MockBean
    private MemberService memberService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser
    public void modifyMemberNickname() throws Exception {
        //given
        String email = "test@test.com";
        String nickname = "modifiedNickname";
        Principal principal = Mockito.mock(Principal.class);
        given(principal.getName()).willReturn(email);

        //when
        //then
        mockMvc.perform(patch("/api/mypage/nickname")
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .content(nickname))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.nickname").value(nickname));
    }

}