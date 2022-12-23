package com.gaethering.gaetheringserver.domain.chat.controller;

import static com.gaethering.gaetheringserver.domain.chat.exception.errorcode.ChatErrorCode.CHAT_ROOM_NOT_FOUND;
import static com.gaethering.gaetheringserver.domain.member.exception.errorcode.MemberErrorCode.MEMBER_NOT_FOUND;
import static com.gaethering.gaetheringserver.domain.pet.exception.errorcode.PetErrorCode.REPRESENTATIVE_PET_NOT_FOUND;
import static com.gaethering.gaetheringserver.member.util.ApiDocumentUtils.getDocumentRequest;
import static com.gaethering.gaetheringserver.member.util.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gaethering.gaetheringserver.config.SecurityConfig;
import com.gaethering.gaetheringserver.domain.chat.dto.ChatMessageResponse;
import com.gaethering.gaetheringserver.domain.chat.dto.ChatRoomInfo;
import com.gaethering.gaetheringserver.domain.chat.dto.ChatRoomMemberInfo;
import com.gaethering.gaetheringserver.domain.chat.dto.MakeChatRoomRequest;
import com.gaethering.gaetheringserver.domain.chat.dto.WalkingTimeInfo;
import com.gaethering.gaetheringserver.domain.chat.exception.ChatRoomNotFoundException;
import com.gaethering.gaetheringserver.domain.chat.service.ChatService;
import com.gaethering.gaetheringserver.domain.member.exception.member.MemberNotFoundException;
import com.gaethering.gaetheringserver.domain.member.jwt.JwtAuthenticationFilter;
import com.gaethering.gaetheringserver.domain.pet.exception.RepresentativePetNotFoundException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
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

    private static List<WalkingTimeInfo> walkingTimeInfos;
    private static List<ChatRoomMemberInfo> chatRoomMemberInfos;

    @BeforeAll
    public static void setData() {
        WalkingTimeInfo walkingTime1 = WalkingTimeInfo.builder()
            .dayOfWeek("월")
            .time("2020-11-20 11:30 ~ 2020-11-20 13:30")
            .build();
        WalkingTimeInfo walkingTime2 = WalkingTimeInfo.builder()
            .dayOfWeek("화")
            .time("2020-11-20 11:30 ~ 2020-11-20 13:30")
            .build();
        walkingTimeInfos = List.of(walkingTime1, walkingTime2);

        ChatRoomMemberInfo chatRoomMemberInfo1 = ChatRoomMemberInfo.builder()
            .id(1L)
            .nickname("nickname1")
            .representPetImageUrl("url1").build();
        ChatRoomMemberInfo chatRoomMemberInfo2 = ChatRoomMemberInfo.builder()
            .id(2L)
            .nickname("nickname2")
            .representPetImageUrl("url2").build();
        chatRoomMemberInfos = List.of(chatRoomMemberInfo1, chatRoomMemberInfo2);
    }

    @Test
    @WithMockUser
    @DisplayName("채팅방 만들기 성공")
    public void makeChatRoom_Success() throws Exception {
        //given
        MakeChatRoomRequest request = MakeChatRoomRequest.builder()
            .description("설명")
            .walkingTimes(walkingTimeInfos)
            .build();

        willDoNothing().given(chatService).makeChatRoom(anyString(),
            any(MakeChatRoomRequest.class));

        //when
        //then
        mockMvc.perform(post("/api/chat/room")
                .content(objectMapper.writeValueAsString(request))
                .contentType(APPLICATION_JSON)
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
        MakeChatRoomRequest request = MakeChatRoomRequest.builder()
            .description("설명")
            .walkingTimes(walkingTimeInfos)
            .build();

        willThrow(new MemberNotFoundException()).given(chatService).makeChatRoom(anyString(),
            any(MakeChatRoomRequest.class));

        //when
        //then
        mockMvc.perform(post("/api/chat/room")
                .content(objectMapper.writeValueAsString(request))
                .contentType(APPLICATION_JSON)
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
    public void getChatRoomInfoSuccess() throws Exception {
        //given
        ChatRoomInfo chatRoomInfo = ChatRoomInfo.builder()
            .name("name")
            .roomKey("roomKey")
            .description("description")
            .maxParticipant(100)
            .walkingTimeInfos(walkingTimeInfos)
            .chatRoomMemberInfos(chatRoomMemberInfos).build();
        given(chatService.getChaRoomInformation(anyString()))
            .willReturn(chatRoomInfo);

        //when
        //then
        mockMvc.perform(get("/api/chat/room/{roomKey}", 1)
                .pathInfo("/api/chat/room/{roomKey}")
                .contentType(APPLICATION_JSON)
                .header("Authorization", "accessToken"))
            .andExpect(jsonPath("$.name").value(chatRoomInfo.getName()))
            .andExpect(jsonPath("$.roomKey").value(chatRoomInfo.getRoomKey()))
            .andExpect(jsonPath("$.description").value(chatRoomInfo.getDescription()))
            .andExpect(jsonPath("$.maxParticipant").value(String.valueOf(chatRoomInfo.getMaxParticipant())))
            .andExpect(jsonPath("$.walkingTimeInfos[0].dayOfWeek").value(walkingTimeInfos.get(0).getDayOfWeek()))
            .andExpect(jsonPath("$.walkingTimeInfos[0].time").value(walkingTimeInfos.get(0).getTime()))
            .andExpect(jsonPath("$.chatRoomMemberInfos[0].id").value(String.valueOf(chatRoomMemberInfos.get(0).getId())))
            .andExpect(jsonPath("$.chatRoomMemberInfos[0].nickname").value(chatRoomMemberInfos.get(0).getNickname()))
            .andExpect(jsonPath("$.chatRoomMemberInfos[0].representPetImageUrl").value(
                chatRoomMemberInfos.get(0).getRepresentPetImageUrl()))

            .andDo(print())
            .andDo(document("chat/get-chat-information/success",
                getDocumentRequest(),
                getDocumentResponse(),
                pathParameters(parameterWithName("roomKey").description("조회할 채팅방 키값")),
                requestHeaders(headerWithName("Authorization").description("Access Token"))));
    }

    @Test
    @WithMockUser
    public void getChatRoomInfoChatRoomNotFoundFailure() throws Exception {
        //given
        given(chatService.getChaRoomInformation(anyString()))
            .willThrow(new ChatRoomNotFoundException());

        //when
        //then
        mockMvc.perform(get("/api/chat/room/{roomKey}", 1)
                .pathInfo("/api/chat/room/{roomKey}")
                .contentType(APPLICATION_JSON)
                .header("Authorization", "accessToken"))
            .andExpect(status().is4xxClientError())
            .andExpect(jsonPath("$.code").value(CHAT_ROOM_NOT_FOUND.getCode()))
            .andExpect(jsonPath("$.message").value(CHAT_ROOM_NOT_FOUND.getMessage()))

            .andDo(print())
            .andDo(document("chat/get-chat-information/failure/chat-room-not-found",
                getDocumentRequest(),
                getDocumentResponse(),
                pathParameters(parameterWithName("roomKey").description("조회할 채팅방 키값")),
                requestHeaders(
                    headerWithName("Authorization").description("Access Token"))
            ));
    }

    @Test
    @WithMockUser
    public void getChatRoomInfoRepresentativePetNotFoundFailure() throws Exception {
        //given
        given(chatService.getChaRoomInformation(anyString()))
            .willThrow(new RepresentativePetNotFoundException());

        //when
        //then
        mockMvc.perform(get("/api/chat/room/{roomKey}", 1)
                .pathInfo("/api/chat/room/{roomKey}")
                .contentType(APPLICATION_JSON)
                .header("Authorization", "accessToken"))
            .andExpect(status().is4xxClientError())
            .andExpect(jsonPath("$.code").value(REPRESENTATIVE_PET_NOT_FOUND.getCode()))
            .andExpect(jsonPath("$.message").value(REPRESENTATIVE_PET_NOT_FOUND.getMessage()))

            .andDo(print())
            .andDo(document("chat/get-chat-information/failure/representative-pet-not-found",
                getDocumentRequest(),
                getDocumentResponse(),
                pathParameters(parameterWithName("roomKey").description("조회할 채팅방 키값")),
                requestHeaders(
                    headerWithName("Authorization").description("Access Token"))
            ));
    }

    @Test
    public void getChatHistorySuccess() {
        //given

        //when

        //then

    }

    @Test
    @WithMockUser
    public void getChatHistoryChatRoomNotFoundFailure() throws Exception {
        //given
        List<ChatMessageResponse> messageResponses = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            ChatMessageResponse messageResponse = ChatMessageResponse.builder().memberId((long) i).content("content" + i)
                .createdAt(Timestamp.valueOf(LocalDateTime.now())).build();
            messageResponses.add(messageResponse);
        }
        given(chatService.getChatHistory(anyString()))
            .willReturn(messageResponses);

        //when
        //then
        mockMvc.perform(get("/api/chat/room/{roomKey}/history", 1)
                .pathInfo("/api/chat/room/{roomKey}/history")
                .contentType(APPLICATION_JSON)
                .header("Authorization", "accessToken"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].memberId").value(String.valueOf(messageResponses.get(0).getMemberId())))
            .andExpect(jsonPath("$[1].memberId").value(String.valueOf(messageResponses.get(1).getMemberId())))
            .andExpect(jsonPath("$[2].memberId").value(String.valueOf(messageResponses.get(2).getMemberId())))

            .andDo(print())
            .andDo(document("chat/get-chat-history/success",
                getDocumentRequest(),
                getDocumentResponse(),
                pathParameters(parameterWithName("roomKey").description("조회할 채팅방 키값")),
                requestHeaders(
                    headerWithName("Authorization").description("Access Token"))
            ));
    }

}