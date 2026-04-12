package com.website.main.mapper;

import org.springframework.stereotype.Component;

import com.website.main.dto.Chat.ChatResponseDTO;
import com.website.main.model.Chat;
import com.website.main.model.Event;
import com.website.main.model.User;
import com.website.main.repository.EventRepository;

@Component
public class ChatMapper {

    EventRepository eventRepository;

    public ChatMapper(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }
    
    public ChatResponseDTO toDTO(Chat chat) {

        if (chat == null) return null;

        ChatResponseDTO responseDTO = new ChatResponseDTO();
        responseDTO.setId(chat.getId());
        responseDTO.setName(chat.getName());
        responseDTO.setParticipantNames(chat.getUsers().stream()
                    .map(User::getName).toList());

        Event event = eventRepository.findByChatId(chat.getId()).orElse(null);
        if (event != null) responseDTO.setEventId(event.getId());

        return responseDTO;
    }
}