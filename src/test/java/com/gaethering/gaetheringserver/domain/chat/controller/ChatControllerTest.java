package com.gaethering.gaetheringserver.domain.chat.controller;

import static com.gaethering.gaetheringserver.domain.chat.exception.errorcode.ChatErrorCode.CHAT_ROOM_NOT_FOUND;
import static com.gaethering.gaetheringserver.domain.member.exception.errorcode.MemberErrorCode.MEMBER_NOT_FOUND;
import static com.gaethering.gaetheringserver.member.util.ApiDocumentUtils.getDocumentRequest;
import static com.gaethering.gaetheringserver.member.util.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gaethering.gaetheringserver.config.SecurityConfig;
import com.gaethering.gaetheringserver.domain.chat.dto.MakeChatRoomRequest;
import com.gaethering.gaetheringserver.domain.chat.dto.WalkingTimeInfo;
import com.gaethering.gaetheringserver.domain.chat.exception.ChatRoomNotFoundException;
import com.gaethering.gaetheringserver.domain.chat.service.ChatService;
import com.gaethering.gaetheringserver.domain.member.exception.member.MemberNotFoundException;
import com.gaethering.gaetheringserver.domain.member.jwt.JwtAuthenticationFilter;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = ChatController.class, excludeFilters = {
    @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
        classes = {SecurityConfig.class, JwtAuthenticationFilter.class})
})
@ActiveProfiles("test")
@AutoConfigureRestDocs
class ChatControllerTest {

    @MockBean
    private ChatService chatService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser
    @DisplayName("채팅방 만들기 성공")
    public void makeChatRoom_Success() throws Exception {
        //given
        WalkingTimeInfo walkingTime1 = WalkingTimeInfo.builder()
            .dayOfWeek("월")
            .time("2020-11-20 11:30 ~ 2020-11-20 13:30")
            .build();

        WalkingTimeInfo walkingTime2 = WalkingTimeInfo.builder()
            .dayOfWeek("화")
            .time("2020-11-20 11:30 ~ 2020-11-20 13:30")
            .build();

        MakeChatRoomRequest request = MakeChatRoomRequest.builder()
            .name("채팅방 이름")
            .maxParticipantCount(6)
            .description("설명")
            .walkingTimes(List.of(walkingTime1, walkingTime2))
            .build();

        willDoNothing().given(chatService).makeChatRoom(anyString(),
            any(MakeChatRoomRequest.class));

        //when
        //then
        mockMvc.perform(post("/api/chat/room")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .header("Authorization", "accessToken"))
            .andExpect(status().isCreated())

            .andDo(print())
            .andDo(document("chat/make-chatroom/success",
                getDocumentRequest(),
                getDocumentResponse(),
                requestHeaders(
                    headerWithName("Authorization").description("Access Token"))
            ));
    }

    @Test
    @WithMockUser
    @DisplayName("채팅방 만들기 실패 - 사용자를 찾을 수 없음")
    public void makeChatRoom_ExceptionThrown_MemberNotFound() throws Exception {
        //given
        WalkingTimeInfo walkingTime1 = WalkingTimeInfo.builder()
            .dayOfWeek("월")
            .time("2020-11-20 11:30 ~ 2020-11-20 13:30")
            .build();

        WalkingTimeInfo walkingTime2 = WalkingTimeInfo.builder()
            .dayOfWeek("화")
            .time("2020-11-20 11:30 ~ 2020-11-20 13:30")
            .build();

        MakeChatRoomRequest request = MakeChatRoomRequest.builder()
            .name("채팅방 이름")
            .maxParticipantCount(6)
            .description("설명")
            .walkingTimes(List.of(walkingTime1, walkingTime2))
            .build();

        willThrow(new MemberNotFoundException()).given(chatService).makeChatRoom(anyString(),
            any(MakeChatRoomRequest.class));

        //when
        //then
        mockMvc.perform(post("/api/chat/room")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .header("Authorization", "accessToken"))
            .andExpect(status().is4xxClientError())
            .andExpect(jsonPath("$.code").value(MEMBER_NOT_FOUND.getCode()))
            .andExpect(jsonPath("$.message").value(MEMBER_NOT_FOUND.getMessage()))

            .andDo(print())
            .andDo(document("chat/make-chatroom/failure/member-not-found",
                getDocumentRequest(),
                getDocumentResponse(),
                requestHeaders(
                    headerWithName("Authorization").description("Access Token"))
            ));
    }

    @Test
    @WithMockUser
    @DisplayName("채팅방 삭제 성공")
    public void deleteChatRoom_Success() throws Exception {
        //given
        willDoNothing().given(chatService).deleteChatRoom(anyString(), anyString());

        //when
        //then
        mockMvc.perform(delete("/api/chat/room/{chatRoomKey}", "testKey")
                .with(csrf())
                .header("Authorization", "accessToken"))
            .andExpect(status().isOk())
            .andDo(print())
            .andDo(document("chat/delete-chatroom/success",
                getDocumentRequest(),
                getDocumentResponse(),
                pathParameters(parameterWithName("chatRoomKey").description("채팅방 키 값")),
                requestHeaders(
                    headerWithName("Authorization").description("Access Token"))
            ));
    }

    @Test
    @WithMockUser
    @DisplayName("채팅방 삭제 실패 - 사용자를 찾을 수 없음")
    public void deleteChatRoom_ExceptionThrown_MemberNotFound() throws Exception {
        //given
        willThrow(new MemberNotFoundException()).given(chatService)
            .deleteChatRoom(anyString(), anyString());

        //when
        //then
        mockMvc.perform(delete("/api/chat/room/{chatRoomKey}", "testKey")
                .with(csrf())
                .header("Authorization", "accessToken"))
            .andExpect(status().is4xxClientError())
            .andExpect(jsonPath("$.code").value(MEMBER_NOT_FOUND.getCode()))
            .andExpect(jsonPath("$.message").value(MEMBER_NOT_FOUND.getMessage()))

            .andDo(print())
            .andDo(document("chat/delete-chatroom/failure/member-not-found",
                getDocumentRequest(),
                getDocumentResponse(),
                pathParameters(parameterWithName("chatRoomKey").description("채팅방 키 값")),
                requestHeaders(
                    headerWithName("Authorization").description("Access Token"))
            ));
    }

    @Test
    @WithMockUser
    @DisplayName("채팅방 삭제 실패 - 채팅방을 찾을 수 없음")
    public void deleteChatRoom_ExceptionThrown_ChatRoomNotFound() throws Exception {
        //given
        willThrow(new ChatRoomNotFoundException()).given(chatService)
            .deleteChatRoom(anyString(), anyString());

        //when
        //then
        mockMvc.perform(delete("/api/chat/room/{chatRoomKey}", "wrong-chatroom-key")
                .with(csrf())
                .header("Authorization", "accessToken"))
            .andExpect(status().is4xxClientError())
            .andExpect(jsonPath("$.code").value(CHAT_ROOM_NOT_FOUND.getCode()))
            .andExpect(jsonPath("$.message").value(CHAT_ROOM_NOT_FOUND.getMessage()))

            .andDo(print())
            .andDo(document("chat/delete-chatroom/failure/chat-room-not-found",
                getDocumentRequest(),
                getDocumentResponse(),
                pathParameters(parameterWithName("chatRoomKey").description("채팅방 키 값")),
                requestHeaders(
                    headerWithName("Authorization").description("Access Token"))
            ));
    }

}