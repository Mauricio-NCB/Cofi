package com.website.main.mapper;

import org.springframework.stereotype.Component;

import com.website.main.dto.Chat.ChatResponseDTO;
import com.website.main.model.Chat;
import com.website.main.model.User;

@Component
public class ChatMapper {
    
    public ChatResponseDTO toDTO(Chat chat) {

        if (chat == null) return null;

        ChatResponseDTO responseDTO = new ChatResponseDTO();
        responseDTO.setId(chat.getId());
        responseDTO.setType(chat.getType());
        responseDTO.setParticipantNames(chat.getUsers().stream()
                    .map(User::getName).toList());

        return responseDTO;
    }
}