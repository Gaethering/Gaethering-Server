package com.gaethering.gaetheringserver.member.controller;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.gaethering.gaetheringserver.config.SecurityConfig;
import com.gaethering.gaetheringserver.filter.JwtAuthenticationFilter;
import com.gaethering.gaetheringserver.member.dto.FollowResponse;
import com.gaethering.gaetheringserver.member.service.FollowService;
import java.security.Principal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = FollowController.class, excludeFilters = {
    @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
        classes = {SecurityConfig.class, JwtAuthenticationFilter.class})
})
class FollowControllerTest {

    @MockBean
    private FollowService followService;
    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser
    public void createFollow() throws Exception {
        //given
        Principal principal = Mockito.mock(Principal.class);
        given(principal.getName()).willReturn("test@test.com");

        //when
        //then
        mockMvc.perform(post("/api/members/1/follow")
                .with(csrf()))
            .andDo(print())
            .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser
    public void getFollowers() throws Exception {
        //given
        List<FollowResponse> followResponses = createFollowResponses();
        given(followService.getFollowers(anyLong()))
            .willReturn(followResponses);

        //when
        //then
        checkPerform("/api/members/1/follower", followResponses.get(0), followResponses.get(1));
    }

    @Test
    @WithMockUser
    public void getFollowings() throws Exception {
        //given
        List<FollowResponse> followResponses = createFollowResponses();
        given(followService.getFollowees(anyLong()))
            .willReturn(followResponses);

        //when
        //then
        checkPerform("/api/members/1/following", followResponses.get(0), followResponses.get(1));
    }

    private void checkPerform(String url, FollowResponse followResponse1,
        FollowResponse followResponse2) throws Exception {
        mockMvc.perform(get(url)
                .with(csrf()))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(followResponse1.getId()))
            .andExpect(jsonPath("$[0].name").value(followResponse1.getName()))
            .andExpect(jsonPath("$[0].nickname").value(followResponse1.getNickname()))
            .andExpect(jsonPath("$[1].id").value(followResponse2.getId()))
            .andExpect(jsonPath("$[1].name").value(followResponse2.getName()))
            .andExpect(jsonPath("$[1].nickname").value(followResponse2.getNickname()));
    }

    private List<FollowResponse> createFollowResponses() {
        FollowResponse followResponse1 = FollowResponse.builder().id(1L).name("name1")
            .nickname("nickname1")
            .build();
        FollowResponse followResponse2 = FollowResponse.builder().id(2L).name("name2")
            .nickname("nickname2")
            .build();
        return List.of(followResponse1, followResponse2);
    }
}