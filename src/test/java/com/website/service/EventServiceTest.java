package com.website.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.website.main.dto.Category.CategoryResponseDTO;
import com.website.main.dto.Event.EventCalendarDTO;
import com.website.main.dto.Event.EventCreateDTO;
import com.website.main.dto.Event.EventResponseDTO;
import com.website.main.mapper.EventMapper;
import com.website.main.model.Chat;
import com.website.main.model.Event;
import com.website.main.model.User;
import com.website.main.model.Achievement.AchievementType;
import com.website.main.repository.ChatRepository;
import com.website.main.repository.EventRepository;
import com.website.main.repository.UserRepository;
import com.website.main.service.AchievementService;
import com.website.main.service.EventService;

@ExtendWith(MockitoExtension.class)
@DisplayName("EventService test")
public class EventServiceTest {
    
    @Mock private EventRepository eventRepository;
    @Mock private UserRepository userRepository;
    @Mock private ChatRepository chatRepository;
    @Mock private AchievementService achievementService;
    @Mock private EventMapper eventMapper;

    @InjectMocks private EventService eventService;

    private User creator;
    private User participant;
    private Event event;
    private EventResponseDTO eventResponseDTO;
    private EventCreateDTO eventCreateDTO;

    @BeforeEach
    void setup() {
        creator = new User();
        creator.setId(1);
        creator.setName("John");
        creator.setLastname("Doe");
        creator.setPostcode("28001");

        participant = new User();
        participant.setId(2);
        participant.setName("Jane");
        participant.setLastname("Doe");

        event = new Event();
        event.setId(1);
        event.setTitle("Test Event");
        event.setDescription("This is a test event");
        event.setDateEvent(LocalDate.now().plusDays(7));
        event.setTimeEvent(LocalTime.of(10, 0));
        event.setMaxCapacity(10);
        event.setPostcode("28001");
        event.setUser(creator);
        event.setParticipants(new ArrayList<>(List.of(creator)));
        event.setCategories(new ArrayList<>());

        eventResponseDTO = new EventResponseDTO();
        eventResponseDTO.setId(1);
        eventResponseDTO.setTitle("Test Event");
        eventResponseDTO.setAvailableSpots(9);
        eventResponseDTO.setIsUserCreator(false);
        eventResponseDTO.setIsUserParticipant(false);

        eventCreateDTO = new EventCreateDTO();
        eventCreateDTO.setTitle("Test Event");
        eventCreateDTO.setDescription("This is a test event");
        eventCreateDTO.setDateEvent(LocalDate.now().plusDays(7));
        eventCreateDTO.setTimeEvent(LocalTime.of(10, 0));
        eventCreateDTO.setMaxCapacity(10);
        eventCreateDTO.setPostcode("28001");

    }

    // ----------------------------------------------------------------------------------------------
    
    @Nested
    @DisplayName("Crear un evento")
    class Create {

        @Test
        @DisplayName("Debería crear un evento correctamente")
        void creaEventoCorrectamente() {
            
            List<CategoryResponseDTO> categoriesDTO = new ArrayList<>();

            when(userRepository.findById(1)).thenReturn(Optional.of(creator));
            when(eventRepository.save(any(Event.class))).thenReturn(event);
            when(chatRepository.save(any(Chat.class))).thenReturn(new Chat());
            when(eventMapper.toDTO(any(Event.class))).thenReturn(eventResponseDTO);

            EventResponseDTO result = eventService.save(eventCreateDTO, categoriesDTO, 1);

            assertNotNull(result);
            assertEquals("Test Event", result.getTitle());

            verify(eventRepository, times(2)).save(any(Event.class));
            verify(chatRepository, times(1)).save(any(Chat.class));
            verify(achievementService, times(1)).unlockAchievement(1, AchievementType.FIRST_EVENT);
        }

        @Test
        @DisplayName("Debería lanzar una excepción si el usuario no existe")
        void usuarioNoExiste_lanzaExcepcion() {
            
            when(userRepository.findById(99)).thenReturn(Optional.empty());

            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                eventService.save(eventCreateDTO, new ArrayList<>(), 99);
            });

            assertEquals("Usuario no encontrado", exception.getMessage());
            verify(eventRepository, never()).save(any());
        }

        @Test
        @DisplayName("Debería lanzar una excepción si el código postal está vacío")
        void codigoPostalVacio_lanzaExcepcion() {
            
            eventCreateDTO.setPostcode("");
            
            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                eventService.save(eventCreateDTO, new ArrayList<>(), 1);
            });

            assertEquals("Debe seleccionar un código postal", exception.getMessage());

            verify(eventRepository, never()).save(any());
            verify(chatRepository, never()).save(any());
        }   

        @Test
        @DisplayName("Debería asignar el estado 'proximo' si el evento es en el futuro")
        void eventoFuturo_asignaEstadoProximo() {

            eventCreateDTO.setDateEvent(LocalDate.now().plusDays(7));
            eventCreateDTO.setTimeEvent(LocalTime.of(10, 0));

            when(userRepository.findById(1)).thenReturn(Optional.of(creator));
            when(eventRepository.save(any(Event.class))).thenReturn(event);
            when(chatRepository.save(any(Chat.class))).thenReturn(new Chat());
            when(eventMapper.toDTO(any(Event.class))).thenReturn(eventResponseDTO);

            eventService.save(eventCreateDTO, new ArrayList<>(), 1);

            verify(chatRepository, times(1)).save(any(Chat.class));
            verify(eventRepository, atLeastOnce()).save(any(Event.class));
        }

        @Test
        @DisplayName("Debería asignar el estado 'en_curso' si el evento es en curso")
        void eventoEnCurso_asignaEstadoEnCurso() {

            eventCreateDTO.setDateEvent(LocalDate.now());
            eventCreateDTO.setTimeEvent(LocalTime.now().minusHours(1));

            when(userRepository.findById(1)).thenReturn(Optional.of(creator));
            when(eventRepository.save(any(Event.class))).thenReturn(event);
            when(chatRepository.save(any(Chat.class))).thenReturn(new Chat());
            when(eventMapper.toDTO(any(Event.class))).thenReturn(eventResponseDTO);

            eventService.save(eventCreateDTO, new ArrayList<>(), 1);

            verify(chatRepository, times(1)).save(any(Chat.class));
            verify(eventRepository, atLeastOnce()).save(any(Event.class));
        }
        
        @Test
        @DisplayName("Debería asignar el estado 'terminado' si el evento es pasado")
        void eventoPasado_asignaEstadoTerminado() {

            eventCreateDTO.setDateEvent(LocalDate.now().minusDays(1));
            eventCreateDTO.setTimeEvent(LocalTime.of(10, 0));

            when(userRepository.findById(1)).thenReturn(Optional.of(creator));
            when(eventRepository.save(any(Event.class))).thenReturn(event);
            when(chatRepository.save(any(Chat.class))).thenReturn(new Chat());
            when(eventMapper.toDTO(any(Event.class))).thenReturn(eventResponseDTO);

            eventService.save(eventCreateDTO, new ArrayList<>(), 1);

            verify(chatRepository, times(1)).save(any(Chat.class));
            verify(eventRepository, atLeastOnce()).save(any(Event.class));
        }
    }

    // ----------------------------------------------------------------------------------------------

    @Nested
    @DisplayName("Unirse a un evento")
    class Join {

        @Test
        @DisplayName("Debería unirse a un evento correctamente")
        void unirseEventoCorrectamente() {

            Chat chat = new Chat();
            chat.setId(1);
            chat.setUsers(new ArrayList<>(List.of(creator)));
            event.setChatId(1);

            when(eventRepository.findById(1)).thenReturn(Optional.of(event));
            when(userRepository.findById(2)).thenReturn(Optional.of(participant));
            when(eventRepository.save(any(Event.class))).thenReturn(event);
            when(chatRepository.findById(1)).thenReturn(Optional.of(chat));
            when(chatRepository.save(any(Chat.class))).thenReturn(chat);
            when(eventMapper.toDTO(any(Event.class))).thenReturn(eventResponseDTO);

            EventResponseDTO result = eventService.joinEvent(1, 2);

            assertNotNull(result);
            assertTrue(event.getParticipants().contains(participant));
            assertTrue(chat.getUsers().contains(participant));

            verify(eventRepository, times(1)).save(event);
            verify(chatRepository, times(1)).save(chat);
        }

        @Test
        @DisplayName("Debería lanzar una excepción si no hay plazas disponibles")
        void noHayPlazas_lanzaExcepcion() {

            event.setMaxCapacity(1);

            when(eventRepository.findById(1)).thenReturn(Optional.of(event));
            when(userRepository.findById(2)).thenReturn(Optional.of(participant));

            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                eventService.joinEvent(1, 2);
            });
            
            assertEquals("No hay plazas disponibles en este evento", exception.getMessage());
            verify(eventRepository, never()).save(any());
        }

        @Test
        @DisplayName("Debería lanzar una excepción si el usuario ya está registrado en el evento")
        void usuarioYaRegistrado_lanzaExcepcion() {

            event.getParticipants().add(participant);

            when(eventRepository.findById(1)).thenReturn(Optional.of(event));
            when(userRepository.findById(2)).thenReturn(Optional.of(participant));

            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                eventService.joinEvent(1, 2);
            });
            
            assertEquals("Ya estás registrado en este evento", exception.getMessage());
            verify(eventRepository, never()).save(any());
        }

        @Test
        @DisplayName("Debería lanzar una excepción si el evento no existe")
        void eventoNoExiste_lanzaExcepcion() {

            when(eventRepository.findById(99)).thenReturn(Optional.empty());

            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                eventService.joinEvent(99, 2);
            });
            
            assertEquals("Evento no encontrado", exception.getMessage());
            verify(eventRepository, never()).save(any());
        }

    }

    // ----------------------------------------------------------------------------------------------

    @Nested
    @DisplayName("Salir de un evento") 
    class Leave{

        @Test
        @DisplayName("Debería salir de un evento correctamente")
        void salirEventoCorrectamente() {

            event.getParticipants().add(participant);

            Chat chat = new Chat();
            chat.setId(1);
            chat.setUsers(new ArrayList<>(List.of(creator, participant)));
            event.setChatId(1);

            when(eventRepository.findById(1)).thenReturn(Optional.of(event));
            when(userRepository.findById(2)).thenReturn(Optional.of(participant));
            when(eventRepository.save(any(Event.class))).thenReturn(event);
            when(chatRepository.findById(1)).thenReturn(Optional.of(chat));
            when(chatRepository.save(any(Chat.class))).thenReturn(chat);
            when(eventMapper.toDTO(any(Event.class))).thenReturn(eventResponseDTO);

            EventResponseDTO result = eventService.leaveEvent(1, 2);

            assertNotNull(result);
            assertFalse(event.getParticipants().contains(participant));
            assertFalse(chat.getUsers().contains(participant));

            verify(eventRepository, times(1)).save(event);
            verify(chatRepository, times(1)).save(chat);
        }
 
        @Test
        @DisplayName("Debería lanzar una excepción si el usuario no está registrado en el evento")
        void usuarioNoRegistrado_lanzaExcepcion() {

            when(eventRepository.findById(1)).thenReturn(Optional.of(event));
            when(userRepository.findById(2)).thenReturn(Optional.of(participant));

            Exception exception = assertThrows(RuntimeException.class, () -> {
                eventService.leaveEvent(1, 2);
            });
            
            assertEquals("No estás registrado en este evento", exception.getMessage());
            verify(eventRepository, never()).save(any());
            verify(chatRepository, never()).save(any());
        }

    }

    // ----------------------------------------------------------------------------------------------

    @Nested
    @DisplayName("Buscar eventos")
    class FindEvents {

        @Test
        @DisplayName("Debería encontrar todos los eventos")
        void encontrarTodosLosEventos() {

            when(eventRepository.findAll()).thenReturn(List.of(event));
            when(eventMapper.toDTOWithUserId(any(Event.class), any())).thenReturn(eventResponseDTO);

            List<EventResponseDTO> result = eventService.findAll();

            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals("Test Event", result.get(0).getTitle());
        }

        @Test
        @DisplayName("Debería encontrar eventos por código postal")
        void encontrarEventosPorCodigoPostal() {

            when(eventRepository.findByPostcode("28001")).thenReturn(List.of(event));
            when(eventMapper.toDTOWithUserId(any(Event.class), any())).thenReturn(eventResponseDTO);

            List<EventResponseDTO> result = eventService.findByPostcode("28001");

            assertNotNull(result);
            assertEquals(1, result.size());
            
            verify(eventRepository, times(1)).findByPostcode("28001");
        }

        @Test
        @DisplayName("Debería retornar DTOs de calendario para el usuario")
        void encontrarCalendarioEventos() {

            EventCalendarDTO calendarDTO = new EventCalendarDTO();
            calendarDTO.setId(1);
            calendarDTO.setTitle("Test Event");

            when(eventRepository.findByParticipants_Id(1)).thenReturn(List.of(event));
            when(eventMapper.toCalendarDTO(any(Event.class))).thenReturn(calendarDTO);

            List<EventCalendarDTO> result = eventService.findByUserIdForCalendar(1);

            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals("Test Event", result.get(0).getTitle());

        }
    }

}
