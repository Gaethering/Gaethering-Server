package com.gaethering.gaetheringserver.domain.chat.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.gaethering.gaetheringserver.domain.chat.dto.ChatMessageResponse;
import com.gaethering.gaetheringserver.domain.chat.dto.ChatRoomInfo;
import com.gaethering.gaetheringserver.domain.chat.dto.MakeChatRoomRequest;
import com.gaethering.gaetheringserver.domain.chat.dto.MakeChatRoomResponse;
import com.gaethering.gaetheringserver.domain.chat.dto.WalkingTimeInfo;
import com.gaethering.gaetheringserver.domain.chat.entity.ChatMessage;
import com.gaethering.gaetheringserver.domain.chat.entity.ChatRoom;
import com.gaethering.gaetheringserver.domain.chat.entity.ChatroomMember;
import com.gaethering.gaetheringserver.domain.chat.entity.WalkingTime;
import com.gaethering.gaetheringserver.domain.chat.exception.ChatRoomNotFoundException;
import com.gaethering.gaetheringserver.domain.chat.exception.errorcode.ChatErrorCode;
import com.gaethering.gaetheringserver.domain.chat.repository.ChatMessageRepository;
import com.gaethering.gaetheringserver.domain.chat.repository.ChatRoomRepository;
import com.gaethering.gaetheringserver.domain.chat.repository.WalkingTimeRepository;
import com.gaethering.gaetheringserver.domain.member.entity.Member;
import com.gaethering.gaetheringserver.domain.member.exception.errorcode.MemberErrorCode;
import com.gaethering.gaetheringserver.domain.member.exception.member.MemberNotFoundException;
import com.gaethering.gaetheringserver.domain.member.repository.member.MemberRepository;
import com.gaethering.gaetheringserver.domain.pet.entity.Pet;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private ChatRoomRepository chatRoomRepository;

    @Mock
    private ChatMessageRepository chatMessageRepository;

    @Mock
    private WalkingTimeRepository walkingTimeRepository;

    @InjectMocks
    private ChatServiceImpl chatService;

    @Test
    @DisplayName("채팅방 만들기 성공")
    void makeChatRoom_Success() {
        //given
        Member member = Member.builder()
            .id(1L)
            .email("email@gmail.com")
            .nickname("닉네임")
            .build();

        MakeChatRoomRequest request = getMakeChatRoomRequest();

        given(memberRepository.findByEmail(anyString())).willReturn(Optional.of(member));

        ArgumentCaptor<ChatRoom> chatRoomCaptor = ArgumentCaptor.forClass(ChatRoom.class);

        //when
        chatService.makeChatRoom(anyString(), request);

        //then
        verify(chatRoomRepository, times(1)).save(chatRoomCaptor.capture());
    }

    @Test
    @DisplayName("채팅방 만들기 실패_사용자를 찾을 수 없는 경우")
    void makeChatRoom_ExceptionThrown_MemberNotFound() {
        //given
        MakeChatRoomRequest request = getMakeChatRoomRequest();

        given(memberRepository.findByEmail(anyString()))
            .willReturn(Optional.empty());

        //when
        MemberNotFoundException exception = assertThrows(
            MemberNotFoundException.class,
            () -> chatService.makeChatRoom(anyString(), request));

        //then
        assertEquals(MemberErrorCode.MEMBER_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    public void getChaRoomInformationChatRoomNotFoundFailure() {
        //given
        given(chatRoomRepository.findByRoomKey(anyString()))
            .willReturn(Optional.empty());

        //when
        //then
        assertThrows(ChatRoomNotFoundException.class, () -> chatService.getChaRoomInformation(anyString()));
    }

    @Test
    public void getChatRoomInformationSuccess() {
        //given
        Pet pet = Pet.builder().imageUrl("url").isRepresentative(true).build();
        Member member = Member.builder().id(1L).nickname("nickname").pets(new ArrayList<>()).build();
        ChatroomMember chatroomMember = ChatroomMember.builder().member(member).build();
        member.addPet(pet);
        WalkingTime walkingTime = WalkingTime.builder().dayOfWeek("수").time("12 : 00").build();
        ChatRoom chatRoom = ChatRoom.builder()
            .name("chat1")
            .roomKey("key")
            .description("description")
            .maxParticipantCount(10)
            .chatroomMembers(new ArrayList<>())
            .walkingTimes(new ArrayList<>()).build();
        chatRoom.addChatroomMember(chatroomMember);
        chatRoom.addWalkingTime(walkingTime);

        given(chatRoomRepository.findByRoomKey(anyString()))
            .willReturn(Optional.of(chatRoom));

        //when
        ChatRoomInfo chaRoomInformation = chatService.getChaRoomInformation(anyString());

        //then
        assertThat(chaRoomInformation.getRoomKey()).isEqualTo(chatRoom.getRoomKey());
        assertThat(chaRoomInformation.getName()).isEqualTo(chatRoom.getName());
        assertThat(chaRoomInformation.getDescription()).isEqualTo(chatRoom.getDescription());
        assertThat(chaRoomInformation.getMaxParticipant()).isEqualTo(chatRoom.getMaxParticipantCount());
        assertThat(chaRoomInformation.getWalkingTimeInfos().get(0).getDayOfWeek()).isEqualTo(
            walkingTime.getDayOfWeek());
        assertThat(chaRoomInformation.getWalkingTimeInfos().get(0).getTime()).isEqualTo(walkingTime.getTime());
        assertThat(chaRoomInformation.getChatRoomMemberInfos().get(0).getId()).isEqualTo(member.getId());
        assertThat(chaRoomInformation.getChatRoomMemberInfos().get(0).getNickname()).isEqualTo(member.getNickname());
        assertThat(chaRoomInformation.getChatRoomMemberInfos().get(0).getRepresentPetImageUrl()).isEqualTo(
            pet.getImageUrl());
    }

    @Test
    public void getCharHistoryChatRoomNotFoundFailure() {
        //given
        given(chatRoomRepository.findByRoomKey(anyString()))
            .willReturn(Optional.empty());

        //when
        //then
        assertThrows(ChatRoomNotFoundException.class, () -> chatService.getChatHistory(anyString()));
    }

    @Test
    public void getCharHistorySuccess() {
        //given
        Member member = Member.builder().id(1L).build();
        ChatMessage message1 = ChatMessage.builder().content("content1").member(member).createdAt(LocalDateTime.MAX)
            .build();
        ChatMessage message2 = ChatMessage.builder().content("content2").member(member).createdAt(LocalDateTime.MIN)
            .build();
        ChatRoom chatRoom = ChatRoom.builder()
            .chatMessages(new ArrayList<>()).build();
        chatRoom.addChatMessage(message1);
        chatRoom.addChatMessage(message2);

        given(chatRoomRepository.findByRoomKey(anyString()))
            .willReturn(Optional.of(chatRoom));

        //when
        List<ChatMessageResponse> chatHistory = chatService.getChatHistory(anyString());

        //then
        assertThat(chatHistory.get(0).getMemberId()).isEqualTo(member.getId());
        assertThat(chatHistory.get(0).getContent()).isEqualTo(message1.getContent());
        assertThat(chatHistory.get(1).getMemberId()).isEqualTo(member.getId());
        assertThat(chatHistory.get(1).getContent()).isEqualTo(message2.getContent());
        assertThat(chatHistory.get(0).getCreatedAt().compareTo(chatHistory.get(1).getCreatedAt()) < 0).isTrue();
    }

    @Test
    @DisplayName("채팅방 삭제 성공")
    void deleteChatRoom_Success() {
        //given
        Member member = Member.builder()
            .id(1L)
            .email("email@gmail.com")
            .nickname("닉네임")
            .build();

        WalkingTime walkingTime1 = WalkingTime.builder()
            .dayOfWeek("월")
            .time("2020-11-20 11:30 ~ 2020-11-20 13:30")
            .build();

        WalkingTime walkingTime2 = WalkingTime.builder()
            .dayOfWeek("화")
            .time("2020-11-20 11:30 ~ 2020-11-20 13:30")
            .build();

        ChatMessage chatMessage1 = ChatMessage.builder()
            .content("메시지1")
            .build();

        ChatMessage chatMessage2 = ChatMessage.builder()
            .content("메시지1")
            .build();

        ChatRoom chatRoom = ChatRoom.builder()
            .roomKey(UUID.randomUUID().toString())
            .description("설명")
            .chatMessages(List.of(chatMessage1, chatMessage2))
            .walkingTimes(List.of(walkingTime1, walkingTime2))
            .build();

        given(memberRepository.findByEmail(anyString()))
            .willReturn(Optional.of(member));

        given(chatRoomRepository.findByRoomKey(anyString()))
            .willReturn(Optional.of(chatRoom));

        ArgumentCaptor<ChatRoom> captor = ArgumentCaptor.forClass(ChatRoom.class);

        //when
        chatService.deleteChatRoom(anyString(), chatRoom.getRoomKey());

        //then
        verify(chatMessageRepository).deleteAllByChatRoom(eq(chatRoom));
        verify(walkingTimeRepository).deleteAllByChatRoom(eq(chatRoom));
        verify(chatRoomRepository, times(1)).delete(captor.capture());
    }

    @Test
    @DisplayName("채팅방 삭제 실패_사용자를 찾을 수 없는 경우")
    void deleteChatRoom_ExceptionThrown_MemberNotFound() {
        //given
        given(memberRepository.findByEmail(anyString()))
            .willReturn(Optional.empty());

        //when
        MemberNotFoundException exception = assertThrows(
            MemberNotFoundException.class,
            () -> chatService.deleteChatRoom(anyString(), "testkey"));

        //then
        assertEquals(MemberErrorCode.MEMBER_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("채팅방 삭제 실패_채팅방을 찾을 수 없는 경우")
    void deleteChatRoom_ExceptionThrown_ChatRoomNotFound() {
        //given
        Member member = Member.builder()
            .id(1L)
            .email("email@gmail.com")
            .nickname("닉네임")
            .build();

        given(memberRepository.findByEmail(anyString()))
            .willReturn(Optional.of(member));

        given(chatRoomRepository.findByRoomKey(anyString()))
            .willReturn(Optional.empty());

        //when
        ChatRoomNotFoundException exception = assertThrows(
            ChatRoomNotFoundException.class,
            () -> chatService.deleteChatRoom(anyString(), "testkey"));

        //then
        assertEquals(ChatErrorCode.CHAT_ROOM_NOT_FOUND, exception.getErrorCode());
    }

    private static MakeChatRoomRequest getMakeChatRoomRequest() {
        WalkingTimeInfo walkingTime1 = WalkingTimeInfo.builder()
            .dayOfWeek("월")
            .time("2020-11-20 11:30 ~ 2020-11-20 13:30")
            .build();
        WalkingTimeInfo walkingTime2 = WalkingTimeInfo.builder()
            .dayOfWeek("화")
            .time("2020-11-20 11:30 ~ 2020-11-20 13:30")
            .build();

        return MakeChatRoomRequest.builder()
            .name("채팅방 이름")
            .maxParticipantCount(6)
            .description("설명")
            .walkingTimes(List.of(walkingTime1, walkingTime2))
            .build();
    }

}