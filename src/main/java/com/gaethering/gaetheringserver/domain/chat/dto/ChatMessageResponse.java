package com.gaethering.gaetheringserver.domain.chat.dto;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageResponse {

    private Long memberId;
    private String content;
    private Timestamp createdAt;

    public static ChatMessageResponse makeResponseFromRequest(ChatMessageRequest chatMessageRequest) {
        return ChatMessageResponse.builder()
            .memberId(chatMessageRequest.getMemberId())
            .content(chatMessageRequest.getContent())
            .createdAt(Timestamp.valueOf(LocalDateTime.now()))
            .build();
    }
}
