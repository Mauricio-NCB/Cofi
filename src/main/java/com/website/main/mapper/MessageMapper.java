package com.website.main.mapper;

import com.website.main.model.Message;
import com.website.main.dto.MessageResponseDTO;

public class MessageMapper {
    
    public MessageResponseDTO toDTO(Message message) {
        MessageResponseDTO dto = new MessageResponseDTO();
        dto.setId(message.getId());
        dto.setUserId(message.getUser().getId());
        dto.setUserName(message.getUser().getName());
        dto.setContent(message.getContent());
        dto.setTimestamp(message.getDateSent().toString());
        return dto;
    }
}
