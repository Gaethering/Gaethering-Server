package com.gaethering.gaetheringserver.domain.chat.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageRequest {

    public static final int CONTENT_MAX_LENGTH = 10000;
    public static final String ROOM_KEY_NOT_BLANK_MESSAGE = "room key는 빈 값을 허용되지 않습니다.";
    public static final String MEMBER_ID_NOT_NULL_MESSAGE = "member Id는 빈 값을 허용되지 않습니다.";
    public static final String CONTENT_NOT_EMPTY_MESSAGE = "메세지는 빈 값을 허용하지 않습니다.";
    public static final String CONTENT_LENGTH_MESSAGE = "메세지 길이는 " + CONTENT_MAX_LENGTH + "자 이상 허용하지 않습니다";

    @NotBlank(message = ROOM_KEY_NOT_BLANK_MESSAGE)
    private String roomKey;

    @NotNull(message = MEMBER_ID_NOT_NULL_MESSAGE)
    private Long memberId;

    @NotEmpty(message = CONTENT_NOT_EMPTY_MESSAGE)
    @Length(max = CONTENT_MAX_LENGTH, message = CONTENT_LENGTH_MESSAGE)
    private String content;
}