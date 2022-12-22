package com.gaethering.gaetheringserver.domain.chat.util;

import com.gaethering.gaetheringserver.config.WebSocketConfig;

public class DestinationUtil {

    private static final String ROOM_DESTINATION_PREFIX = WebSocketConfig.DESTINATION_PREFIX + "/room/";

    public static String makeChatRoomDestination(String roomKey) {
        return ROOM_DESTINATION_PREFIX + roomKey;
    }
}
