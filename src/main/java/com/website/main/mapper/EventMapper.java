package com.website.main.mapper;

import com.website.main.model.Event;
import com.website.main.model.Category;
import com.website.main.dto.Event.EventCalendarDTO;
import com.website.main.dto.Event.EventResponseDTO;

public class EventMapper {
    
    public EventResponseDTO toDTO(Event event) {

        if (event == null) return null;

        EventResponseDTO dto = new EventResponseDTO();
        dto.setId(event.getId());
        dto.setDateEvent(event.getDateEvent());
        dto.setTimeEvent(event.getTimeEvent());
        dto.setTitle(event.getTitle());
        dto.setDescription(event.getDescription());
        dto.setMaxCapacity(event.getMaxCapacity());
        dto.setPostcode(event.getPostcode());
        dto.setState(event.getState());
        dto.setChatId(event.getChatId());
        dto.setCreatorId(event.getUser().getId());
        dto.setCategoryNames(
            event.getCategories().stream()
                .map(Category::getName)
                .toList()
        );

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
