package com.gaethering.gaetheringserver.domain.chat.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

import com.gaethering.gaetheringserver.domain.chat.dto.ChatMessageRequest;
import com.gaethering.gaetheringserver.domain.chat.dto.ChatMessageResponse;
import com.gaethering.gaetheringserver.domain.chat.entity.ChatRoom;
import com.gaethering.gaetheringserver.domain.chat.exception.ChatRoomNotFoundException;
import com.gaethering.gaetheringserver.domain.chat.repository.ChatMessageRepository;
import com.gaethering.gaetheringserver.domain.chat.repository.ChatRoomRepository;
import com.gaethering.gaetheringserver.domain.member.entity.Member;
import com.gaethering.gaetheringserver.domain.member.exception.member.MemberNotFoundException;
import com.gaethering.gaetheringserver.domain.member.repository.member.MemberRepository;
import java.util.ArrayList;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

@ExtendWith(MockitoExtension.class)
class ChatMessageServiceImplTest {

    @Mock
    private ChatRoomRepository chatRoomRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private ChatMessageRepository chatMessageRepository;

    @Mock
    private RabbitTemplate messagingTemplate;

    @InjectMocks
    private ChatMessageServiceImpl chatMessageService;

    @Test
    public void sendMemberNotFoundFailure() {
        //given
        ChatMessageRequest request = ChatMessageRequest.builder()
            .memberId(1L)
            .content("test").build();
        given(memberRepository.findById(anyLong()))
            .willReturn(Optional.empty());

        //when
        //then
        assertThrows(MemberNotFoundException.class, () -> chatMessageService.send(request, "roomKey"));
    }

    @Test
    public void sendChatRoomNotFoundFailure() {
        //given
        ChatMessageRequest request = ChatMessageRequest.builder()
            .memberId(1L)
            .content("test").build();
        given(memberRepository.findById(anyLong()))
            .willReturn(Optional.of(Member.builder().build()));
        given(chatRoomRepository.findByRoomKey(anyString()))
            .willReturn(Optional.empty());

        //when
        //then
        assertThrows(ChatRoomNotFoundException.class, () -> chatMessageService.send(request, "roomKey"));
    }

    @Test
    public void sendSuccess() {
        //given
        ChatMessageRequest request = ChatMessageRequest.builder()
            .memberId(1L)
            .content("test").build();
        Member member = Member.builder().build();
        ChatRoom chatRoom = ChatRoom.builder().chatMessages(new ArrayList<>()).build();
        given(memberRepository.findById(anyLong()))
            .willReturn(Optional.of(member));
        given(chatRoomRepository.findByRoomKey(anyString()))
            .willReturn(Optional.of(chatRoom));

        //when
        ChatMessageResponse result = chatMessageService.send(request, "roomKEy");

        //then
        assertThat(result.getMemberId()).isEqualTo(request.getMemberId());
        assertThat(result.getContent()).isEqualTo(request.getContent());
    }
}