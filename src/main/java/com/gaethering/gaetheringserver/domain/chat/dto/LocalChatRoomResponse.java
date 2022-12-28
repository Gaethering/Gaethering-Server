package com.gaethering.gaetheringserver.domain.chat.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocalChatRoomResponse {

    private int numberOfChatRooms;

    private List<ChatRoomListInfo> chatRooms;

}
