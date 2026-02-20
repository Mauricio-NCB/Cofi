package com.website.main.service;

import org.springframework.stereotype.Service;

import com.website.main.model.Event;
import com.website.main.model.User;
import com.website.main.repository.EventRepository;
import com.website.main.repository.UserRepository;

import java.time.LocalDate;
import java.util.List;

@Service
public class EventService {
    
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    public EventService(EventRepository eventRepository, UserRepository userRepository) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
    }

    public List<Event> findAll() {
        // Logica de negocio adicional: en este caso, no requiere

        return eventRepository.findAll();
    }

    public Event save(Event event, Integer idUsuario) {
        User user = userRepository.findById(idUsuario)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        event.setUser(user);

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

        if (event.getCodepostal() == null || event.getCodepostal().isBlank()) {
            throw new RuntimeException("Debe seleccionar un código postal");
        }

        return eventRepository.save(event);
    }

    public Event findById(Integer id) {
        return eventRepository.findById(id).orElse(null);
    }
    
    public List<Event> findByUserId(Integer userId) {
        return eventRepository.findByUserId(userId);
    }

    public List<Event> findByCategoryId(Integer categoryId) {
        return eventRepository.findByCategoryId(categoryId);
    }

}
