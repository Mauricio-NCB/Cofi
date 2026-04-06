package com.website.main.mapper;

import com.website.main.model.Event;
import com.website.main.model.Category;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.website.main.dto.Event.EventCalendarDTO;
import com.website.main.dto.Event.EventResponseDTO;
import com.website.main.dto.User.UserParticipantDTO;

@Component
public class EventMapper {
    
    public EventResponseDTO toDTO(Event event) {
        return toDTOWithUserId(event, null);
    }

    public EventResponseDTO toDTOWithUserId(Event event, Integer userId) {

        if (event == null) return null;

        EventResponseDTO dto = new EventResponseDTO();
        dto.setId(event.getId());
        dto.setDateEvent(event.getDateEvent());
        dto.setTimeEvent(event.getTimeEvent());
        dto.setTitle(event.getTitle());
        dto.setDescription(event.getDescription());
        dto.setMaxCapacity(event.getMaxCapacity());
        
        // Calcular plazas disponibles
        Integer participantCount = event.getParticipants() != null ? event.getParticipants().size() : 0;
        Integer availableSpots = event.getMaxCapacity() - participantCount;
        dto.setAvailableSpots(Math.max(0, availableSpots)); // No mostrar números negativos
        
        // Verificar si el usuario actual participa en el evento
        Boolean isUserParticipant = false;
        if (userId != null && event.getParticipants() != null) {
            isUserParticipant = event.getParticipants().stream()
                    .anyMatch(p -> p.getId().equals(userId));
        }
        dto.setIsUserParticipant(isUserParticipant);
        
        // Verificar si el usuario actual es el creador del evento
        Boolean isUserCreator = false;
        if (userId != null && event.getUser() != null) {
            isUserCreator = event.getUser().getId().equals(userId);
        }
        dto.setIsUserCreator(isUserCreator);
        
        dto.setPostcode(event.getPostcode());
        dto.setState(event.getState());
        dto.setChatId(event.getChatId());
        dto.setCreatorId(event.getUser().getId());
        dto.setCreatorName(event.getUser().getName());
        dto.setCategoryNames(
            event.getCategories() != null 
                ? event.getCategories().stream()
                    .map(Category::getName)
                    .toList()
                : new ArrayList<>()
        );
        dto.setImageUrl(event.getImageUrl());

        // mapear lista de participantes incluyendo al creador
        List<UserParticipantDTO> participants = new ArrayList<>();

        // añadir el creador primero
        UserParticipantDTO creatorDTO = new UserParticipantDTO();
        creatorDTO.setName(event.getUser().getName());
        creatorDTO.setLastName(event.getUser().getLastname());
        participants.add(creatorDTO);

        // añadir el resto de participantes evitando duplicar al creador
        if (event.getParticipants() != null) {
            event.getParticipants().forEach(p -> {
                boolean isCreator = p.getId().equals(event.getUser().getId());
                if (!isCreator) {
                    UserParticipantDTO pdto = new UserParticipantDTO();
                    pdto.setName(p.getName());
                    pdto.setLastName(p.getLastname());
                    participants.add(pdto);
                }
            });
        }

        dto.setParticipants(participants);

        return dto;
    }

    public EventCalendarDTO toCalendarDTO(Event event) {
        EventCalendarDTO dto = new EventCalendarDTO();
        dto.setId(event.getId());
        dto.setTitle(event.getTitle());
        dto.setStart(event.getDateEvent().toString() + "T" + event.getTimeEvent().toString());
        dto.setDescription(event.getDescription());
        dto.setDateEvent(event.getDateEvent().toString());
        dto.setTimeEvent(event.getTimeEvent().toString());
        dto.setMaxCapacity(event.getMaxCapacity());
        dto.setState(event.getState());

        return dto;
    }
}
