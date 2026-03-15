package com.website.main.dto;

import com.website.main.model.Message;

public record MessageDTO(Integer id, Integer userId, String userName, String content, String timestamp) {
    public static MessageDTO fromEntity(Message m) {
        return new MessageDTO(
                m.getId(),
                m.getUser().getId(),
                m.getUser().getName(),
                m.getContent(),
                m.getDateSent().toString()
        );
    }
}
