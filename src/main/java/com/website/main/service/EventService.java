package com.website.main.service;

import org.springframework.stereotype.Service;

import com.website.main.model.Event;
import com.website.main.model.User;
import com.website.main.model.Category;
import com.website.main.repository.EventRepository;
import com.website.main.repository.UserRepository;
import com.website.main.dto.Event.EventCalendarDTO;
import com.website.main.dto.Event.EventCreateDTO;
import com.website.main.dto.Event.EventResponseDTO;
import com.website.main.mapper.EventMapper;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EventService {
    
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final EventMapper eventMapper;

    public EventService(EventRepository eventRepository, UserRepository userRepository, EventMapper eventMapper) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.eventMapper = eventMapper;
    }

    public List<EventResponseDTO> findAll() {
        // Logica de negocio adicional: en este caso, no requiere
        return eventRepository.findAll().stream()
                .map(eventMapper::toDTO)
                .toList();
    }

    public EventResponseDTO save(EventCreateDTO event, List<Category> categories, Integer idUsuario) {
        User user = userRepository.findById(idUsuario)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Event newEvent = new Event();
        newEvent.setDateEvent(event.getDateEvent());
        newEvent.setTimeEvent(event.getTimeEvent());
        newEvent.setTitle(event.getTitle());
        newEvent.setDescription(event.getDescription());
        newEvent.setMaxCapacity(event.getMaxCapacity());
        newEvent.setPostcode(event.getPostcode());
        newEvent.setUser(user);
        newEvent.setCategories(categories);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime eventDateTime = LocalDateTime.of(event.getDateEvent(), event.getTimeEvent());
        LocalDateTime endDateTime = eventDateTime.plusHours(2);  //SUPONIENDO QUE LA DURACIÓN DEL EVENTO ES DE 2 HORAS

        if (now.isBefore(eventDateTime)) newEvent.setState("proximo");
        else if (now.isBefore(endDateTime)) newEvent.setState("en_curso");
        else newEvent.setState("terminado");

        if (event.getPostcode() == null || event.getPostcode().isBlank()) {
            throw new RuntimeException("Debe seleccionar un código postal");
        }

        return eventMapper.toDTO(eventRepository.save(newEvent));
    }

    public EventResponseDTO findById(Integer id) {
        Event event = eventRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Evento no encontrado"));

        return eventMapper.toDTO(event);
    }
    
    public List<EventResponseDTO> findByUserId(Integer userId) {
        return eventRepository.findByUserId(userId).stream()
                .map(eventMapper::toDTO)
                .toList();
    }

    public List<EventResponseDTO> findByCategoryId(Integer categoryId) {
        return eventRepository.findByCategoryId(categoryId).stream()
                .map(eventMapper::toDTO)
                .toList();
    }

    public List<EventCalendarDTO> findByUserIdForCalendar(Integer userId) {
        return eventRepository.findByUserId(userId).stream()
                .map(eventMapper::toCalendarDTO)
                .toList();
    }

}
