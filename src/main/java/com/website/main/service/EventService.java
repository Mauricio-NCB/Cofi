package com.website.main.service;

import org.springframework.stereotype.Service;

import com.website.main.model.Event;
import com.website.main.repository.EventRepository;

import java.time.LocalDate;
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

    public Event save(Event event) {

        LocalDate today = LocalDate.now();

        if (today.isBefore(event.getStartDate())) {
            event.setState("proximo");
        } 
        else if (!today.isAfter(event.getEndDate())) {
            event.setState("en_curso");
        } 
        else {
            event.setState("terminado");
        }

        return eventRepository.save(event);
    }

    public Event findById(Integer id) {
        return eventRepository.findById(id).orElse(null);
    }
    
}
