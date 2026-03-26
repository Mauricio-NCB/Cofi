package com.website.main.service;

import org.springframework.stereotype.Service;

import com.website.main.model.Event;
import com.website.main.model.User;
import com.website.main.model.Achievement.AchievementType;
import com.website.main.repository.EventRepository;
import com.website.main.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EventService {
    
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final AchievementService achievementService;

    public EventService(EventRepository eventRepository, UserRepository userRepository, AchievementService achievementService) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.achievementService = achievementService;
    }

    public List<Event> findAll() {
        // Logica de negocio adicional: en este caso, no requiere

        return eventRepository.findAll();
    }

    public Event save(Event event, Integer idUsuario) {
        User user = userRepository.findById(idUsuario)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        event.setUser(user);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime eventDateTime = LocalDateTime.of(event.getDateEvent(), event.getTimeEvent());

        LocalDateTime endDateTime = eventDateTime.plusHours(2);  //SUPONIENDO QUE LA DURACIÓN DEL EVENTO ES DE 2 HORAS

        if (now.isBefore(eventDateTime)) {
            event.setState("proximo");
        }
        else if (now.isBefore(endDateTime)) {
            event.setState("en_curso");
        }
        else {
            event.setState("terminado");
        }

        if (event.getCodepostal() == null || event.getCodepostal().isBlank()) {
            throw new RuntimeException("Debe seleccionar un código postal");
        }

        Event savedEvent = eventRepository.save(event);
        
        // Desbloquear logro de primer evento
        achievementService.unlockAchievement(idUsuario, AchievementType.FIRST_EVENT);

        return savedEvent;
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
