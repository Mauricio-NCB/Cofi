package com.website.main.service;

import org.springframework.stereotype.Service;

import com.website.main.model.Event;
import com.website.main.repository.EventRepository;

import java.util.List;

@Service
public class EventService {
    
    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public List<Event> findAll() {
        // Logica de negocio adicional: en este caso, no requiere

        return eventRepository.findAll();
    }
    
}
