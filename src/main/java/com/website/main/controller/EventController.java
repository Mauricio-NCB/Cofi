package com.website.main.controller;

import java.time.Year;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import com.website.main.model.Category;
import com.website.main.service.CategoryService;
import com.website.main.service.EventService;
import com.website.main.service.UserService;
import com.website.main.dto.Category.CategoryResponseDTO;
import com.website.main.dto.Event.EventCalendarDTO;
import com.website.main.dto.Event.EventCreateDTO;
import com.website.main.dto.Event.EventResponseDTO;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/eventos")
public class EventController {

    private final EventService eventService;
    private final CategoryService categoryService;
    private final UserService userService;

    public EventController(EventService eventService,
                           CategoryService categoryService,
                           UserService userService) {
        this.eventService = eventService;
        this.categoryService = categoryService;
        this.userService = userService;
    }

    // LISTADO DE EVENTOS (con filtro opcional por categoría)
    @GetMapping
    public String eventos(@RequestParam(required = false) Integer categoria,
                          Model model) {

        // Obtener usuario autenticado
        Integer userId = null;
        String userPostcode = null;
        try {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal instanceof Integer) {
                userId = (Integer) principal;
                // Obtener el user para acceder a su postcode
                var user = userService.findById(userId);
                userPostcode = user.getPostcode();
            }
        } catch (Exception e) {
            // Usuario no autenticado
        }

        model.addAttribute("userName", "Mauricio");
        model.addAttribute("userId", userId);
        model.addAttribute("userPostcode", userPostcode);
        model.addAttribute("isAuthenticated", userId != null);
        model.addAttribute("year", Year.now().getValue());
        model.addAttribute("currentPage", "eventos");

        List<EventResponseDTO> events;
        List<EventResponseDTO> eventsNearby = List.of(); // Eventos cercanos (si usuario tiene postcode)

        if (categoria != null) {
            events = userId != null ? eventService.findByCategoryIdWithUserInfo(categoria, userId) : eventService.findByCategoryId(categoria);
        } else {
            events = userId != null ? eventService.findAllWithUserInfo(userId) : eventService.findAll();
        }
        
        // Si el usuario tiene postcode, obtener eventos cercanos
        if (userPostcode != null && !userPostcode.isEmpty()) {
            eventsNearby = userId != null ? 
                eventService.findByPostcodeWithUserInfo(userPostcode, userId) :
                eventService.findByPostcode(userPostcode);
        }
        
        model.addAttribute("categoriaSeleccionada", categoria);
        model.addAttribute("events", events);
        model.addAttribute("eventsNearby", eventsNearby);
        model.addAttribute("categorias", categoryService.findAll());
        model.addAttribute("event", new EventCreateDTO());

        return "events";
    }

    // MOSTRAR FORMULARIO CREAR EVENTO
    @GetMapping("/crear")
    public String mostrarFormulario(Model model) {

        List<String> codigos = IntStream.rangeClosed(28001, 28055)
                .mapToObj(String::valueOf)
                .toList();

        model.addAttribute("codigosPostales", codigos);
        model.addAttribute("categorias", categoryService.findAll());
        model.addAttribute("event", new EventCreateDTO());

        return "crear-evento";
    }

    // CREAR EVENTO (ManyToMany categorías)
    @PostMapping("/crear")
    public String crearEvento(@ModelAttribute EventCreateDTO event,
                              @RequestParam List<Integer> categories) {

        // Convertimos IDs en entidades Category
        List<CategoryResponseDTO> categoriasSeleccionadas =
                categoryService.findAllById(categories);

        Integer userId = (Integer) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        eventService.save(event, categoriasSeleccionadas, userId);

        return "redirect:/eventos";
    }

    // OBTENER EVENTO POR ID (para modal)
    @GetMapping("/{id}")
    @ResponseBody
    public EventResponseDTO obtenerEvento(@PathVariable Integer id) {
        return eventService.findById(id);
    }

    // UNIRSE A UN EVENTO
    @PostMapping("/{id}/unirse")
    @ResponseBody
    public Object unirseAlEvento(@PathVariable Integer id) {
        try {
            Integer userId = (Integer) SecurityContextHolder
                    .getContext()
                    .getAuthentication()
                    .getPrincipal();

            EventResponseDTO updatedEvent = eventService.joinEvent(id, userId);
            return Map.of("status", "success", "message", "Te has unido al evento correctamente", "event", updatedEvent);
        } catch (Exception e) {
            return Map.of("status", "error", "message", e.getMessage());
        }
    }

    // CALENDARIO
    @GetMapping("/calendario")
    public String calendario(Model model) {
        model.addAttribute("currentPage", "calendario");
        return "calendario";
    }

    // FULL CALENDAR
    @GetMapping("/api/mis-eventos")
    @ResponseBody
    public List<EventCalendarDTO> misEventos() {

        Integer userId = (Integer) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        return eventService.findByUserIdForCalendar(userId);
    }
}