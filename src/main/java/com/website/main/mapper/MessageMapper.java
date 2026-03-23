package com.website.main.mapper;

import org.springframework.stereotype.Component;

import com.website.main.dto.Message.MessageResponseDTO;
import com.website.main.model.Message;

@Component
public class MessageMapper {
    
    public MessageResponseDTO toDTO(Message message) {

        if (message == null) return null;

        MessageResponseDTO dto = new MessageResponseDTO();
        dto.setId(message.getId());
        dto.setUserId(message.getUser().getId());
        dto.setUserName(message.getUser().getName());
        dto.setContent(message.getContent());
        dto.setTimestamp(message.getDateSent().toString());
        return dto;
    }
}
