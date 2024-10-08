package com.e207.woojoobook.api.chat.response;

import com.e207.woojoobook.domain.chat.Chat;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ChatResponse(Long id, Long chatRoomId, Long userId, String content, LocalDateTime createdAt) {

    public static ChatResponse of(Chat chat) {
        return new ChatResponse(
                chat.getId(),
                chat.getChatRoom().getId(),
                chat.getUserId(),
                chat.getContent(),
                chat.getCreatedAt()
        );
    }
}
