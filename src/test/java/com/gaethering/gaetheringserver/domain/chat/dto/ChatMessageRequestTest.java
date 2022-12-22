package com.gaethering.gaetheringserver.domain.chat.dto;

import static com.gaethering.gaetheringserver.domain.chat.dto.ChatMessageRequest.CONTENT_LENGTH_MESSAGE;
import static com.gaethering.gaetheringserver.domain.chat.dto.ChatMessageRequest.MEMBER_ID_NOT_NULL_MESSAGE;
import static com.gaethering.gaetheringserver.domain.chat.dto.ChatMessageRequest.ROOM_KEY_NOT_BLANK_MESSAGE;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ChatMessageRequestTest {

    private Validator validator;

    @BeforeEach
    public void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    public void testNotBlank() {
        //given
        ChatMessageRequest request = ChatMessageRequest.builder()
            .roomKey(" ")
            .memberId(1L)
            .content("test").build();

        //when
        Optional<ConstraintViolation<ChatMessageRequest>> response = validator.validate(request).stream().findFirst();

        //then
        assertThat(response.isPresent()).isTrue();
        assertThat(response.get().getMessage()).isEqualTo(ROOM_KEY_NOT_BLANK_MESSAGE);
    }

    @Test
    public void testNotNull() {
        //given
        ChatMessageRequest request = ChatMessageRequest.builder()
            .roomKey("test")
            .memberId(null)
            .content("test").build();

        //when
        Optional<ConstraintViolation<ChatMessageRequest>> response = validator.validate(request).stream().findFirst();

        //then
        assertThat(response.isPresent()).isTrue();
        assertThat(response.get().getMessage()).isEqualTo(MEMBER_ID_NOT_NULL_MESSAGE);
    }

    @Test
    public void testLength() {
        //given
        String content = makeOverLengthString();
        ChatMessageRequest request = ChatMessageRequest.builder()
            .roomKey("test")
            .memberId(1L)
            .content(content).build();

        //when
        Optional<ConstraintViolation<ChatMessageRequest>> response = validator.validate(request).stream().findFirst();

        //then
        assertThat(response.isPresent()).isTrue();
        assertThat(response.get().getMessage()).isEqualTo(CONTENT_LENGTH_MESSAGE);
    }

    private static String makeOverLengthString() {
        return "a".repeat(ChatMessageRequest.CONTENT_MAX_LENGTH + 1);
    }
}