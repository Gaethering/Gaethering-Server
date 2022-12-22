package com.gaethering.gaetheringserver.domain.chat.entity;

import com.gaethering.gaetheringserver.core.entity.BaseTimeEntity;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoom extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_room_id", nullable = false)
    private Long id;

    private String name;
    private String roomKey;
    private String description;
    private Integer maxParticipantCount;

    @OneToMany(mappedBy = "chatRoom")
    private List<WalkingTime> walkingTimes = new ArrayList<>();

    @OneToMany(mappedBy = "chatRoom")
    private List<ChatMessage> chatMessages = new ArrayList<>();

    @OneToMany(mappedBy = "chatRoom")
    private List<ChatroomMember> chatroomMembers = new ArrayList<>();

    public void setId(Long id) {
        this.id = id;
    }

    public void addWalkingTime(WalkingTime walkingTime) {
        walkingTimes.add(walkingTime);
        walkingTime.setChatRoom(this);
    }

    public void addChatMessage(ChatMessage chatMessage) {
        chatMessages.add(chatMessage);
        chatMessage.setChatRoom(this);
    }

    public void addChatroomMember(ChatroomMember chatroomMember) {
        chatroomMembers.add(chatroomMember);
        chatroomMember.setChatRoom(this);
    }
}