package com.website.main.service;

import org.springframework.stereotype.Service;

import com.website.main.model.Event;
import com.website.main.model.User;
import com.website.main.model.Achievement.AchievementType;
import com.website.main.model.Category;
import com.website.main.repository.EventRepository;
import com.website.main.repository.UserRepository;
import com.website.main.dto.Category.CategoryResponseDTO;
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
    private final AchievementService achievementService;
    private final EventMapper eventMapper;
    

    public EventService(EventRepository eventRepository, UserRepository userRepository, AchievementService achievementService, EventMapper eventMapper) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.achievementService = achievementService;
        this.eventMapper = eventMapper;
    }

    public List<EventResponseDTO> findAll() {
        // Logica de negocio adicional: en este caso, no requiere
        return eventRepository.findAll().stream()
                .map(event -> eventMapper.toDTOWithUserId(event, null))
                .toList();
    }

    public List<EventResponseDTO> findAllWithUserInfo(Integer userId) {
        return eventRepository.findAll().stream()
                .map(event -> eventMapper.toDTOWithUserId(event, userId))
                .toList();
    }

    public EventResponseDTO save(EventCreateDTO event, List<CategoryResponseDTO> categoriesDTO, Integer idUsuario) {
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

        List<Category> categories = categoriesDTO.stream()
                .map(dto -> {
                    Category category = new Category();
                    category.setId(dto.getId());
                    category.setName(dto.getName());
                    return category;
                })
                .toList();

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

        achievementService.unlockAchievement(idUsuario, AchievementType.FIRST_EVENT);
        
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
        return eventRepository.findByCategories_Id(categoryId).stream()
                .map(event -> eventMapper.toDTOWithUserId(event, null))
                .toList();
    }

    public List<EventResponseDTO> findByCategoryIdWithUserInfo(Integer categoryId, Integer userId) {
        return eventRepository.findByCategories_Id(categoryId).stream()
                .map(event -> eventMapper.toDTOWithUserId(event, userId))
                .toList();
    }

    public List<EventCalendarDTO> findByUserIdForCalendar(Integer userId) {
        return eventRepository.findByUserId(userId).stream()
                .map(eventMapper::toCalendarDTO)
                .toList();
    }

    public EventResponseDTO joinEvent(Integer eventId, Integer userId) throws Exception {
        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new Exception("Evento no encontrado"));
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new Exception("Usuario no encontrado"));

        // Validar que hay plazas disponibles
        int participantCount = event.getParticipants() != null ? event.getParticipants().size() : 0;
        if (participantCount >= event.getMaxCapacity()) {
            throw new Exception("No hay plazas disponibles en este evento");
        }

        // Validar que el usuario no esté ya en el evento
        if (event.getParticipants().stream().anyMatch(p -> p.getId().equals(userId))) {
            throw new Exception("Ya estás registrado en este evento");
        }

        // Agregar el usuario a los participantes del evento
        event.getParticipants().add(user);
        eventRepository.save(event);

        return eventMapper.toDTO(event);
    }

    public List<EventResponseDTO> findByPostcode(String postcode) {
        return eventRepository.findByPostcode(postcode).stream()
                .map(event -> eventMapper.toDTOWithUserId(event, null))
                .toList();
    }

    public List<EventResponseDTO> findByPostcodeWithUserInfo(String postcode, Integer userId) {
        return eventRepository.findByPostcode(postcode).stream()
                .map(event -> eventMapper.toDTOWithUserId(event, userId))
                .toList();
    }

}
