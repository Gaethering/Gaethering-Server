package com.gaethering.gaetheringserver.domain.chat.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.gaethering.gaetheringserver.domain.chat.dto.ChatRoomInfo;
import com.gaethering.gaetheringserver.domain.chat.dto.MakeChatRoomRequest;
import com.gaethering.gaetheringserver.domain.chat.dto.WalkingTimeInfo;
import com.gaethering.gaetheringserver.domain.chat.entity.ChatRoom;
import com.gaethering.gaetheringserver.domain.chat.entity.ChatroomMember;
import com.gaethering.gaetheringserver.domain.chat.entity.WalkingTime;
import com.gaethering.gaetheringserver.domain.chat.exception.ChatRoomNotFoundException;
import com.gaethering.gaetheringserver.domain.chat.repository.ChatRoomRepository;
import com.gaethering.gaetheringserver.domain.chat.repository.WalkingTimeRepository;
import com.gaethering.gaetheringserver.domain.member.entity.Member;
import com.gaethering.gaetheringserver.domain.member.exception.errorcode.MemberErrorCode;
import com.gaethering.gaetheringserver.domain.member.exception.member.MemberNotFoundException;
import com.gaethering.gaetheringserver.domain.member.repository.member.MemberRepository;
import com.gaethering.gaetheringserver.domain.pet.entity.Pet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
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
        ArgumentCaptor<Collection<WalkingTime>> walkingTimeCaptor = ArgumentCaptor.forClass(
            Collection.class);

        //when
        chatService.makeChatRoom(anyString(), request);

        //then
        verify(chatRoomRepository, times(1)).save(chatRoomCaptor.capture());
        verify(walkingTimeRepository, times(1)).saveAll(walkingTimeCaptor.capture());

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
        assertThat(chaRoomInformation.getRoomName()).isEqualTo(chatRoom.getName());
        assertThat(chaRoomInformation.getDescription()).isEqualTo(chatRoom.getDescription());
        assertThat(chaRoomInformation.getMaxParticipant()).isEqualTo(chatRoom.getMaxParticipantCount());
        assertThat(chaRoomInformation.getWalkingTimeInfos().get(0).getDayOfWeek()).isEqualTo(
            walkingTime.getDayOfWeek());
        assertThat(chaRoomInformation.getWalkingTimeInfos().get(0).getTime()).isEqualTo(walkingTime.getTime());
        assertThat(chaRoomInformation.getParticipants().get(0).getId()).isEqualTo(member.getId());
        assertThat(chaRoomInformation.getParticipants().get(0).getNickname()).isEqualTo(member.getNickname());
        assertThat(chaRoomInformation.getParticipants().get(0).getRepresentPetImageUrl()).isEqualTo(pet.getImageUrl());
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
            .description("설명")
            .walkingTimes(List.of(walkingTime1, walkingTime2))
            .build();
    }

}