package com.website.main.controller;

import java.time.Year;
import java.util.List;
import java.util.stream.IntStream;

import com.website.main.model.Category;
import com.website.main.service.CategoryService;
import com.website.main.service.EventService;
import com.website.main.dto.EventCalendarDTO;
import com.website.main.dto.EventCreateDTO;
import com.website.main.dto.EventResponseDTO;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/eventos")
public class EventController {

    private final EventService eventService;
    private final CategoryService categoryService;

    public EventController(EventService eventService,
                           CategoryService categoryService) {
        this.eventService = eventService;
        this.categoryService = categoryService;
    }

    // LISTADO DE EVENTOS (con filtro opcional por categoría)
    @GetMapping
    public String eventos(@RequestParam(required = false) Integer categoria,
                          Model model) {

        model.addAttribute("userName", "Mauricio");
        model.addAttribute("year", Year.now().getValue());
        model.addAttribute("currentPage", "eventos");

        List<EventResponseDTO> events;

        if (categoria != null) {
            events = eventService.findByCategoryId(categoria);
        } else {
            events = eventService.findAll();
        }
        
        model.addAttribute("categoriaSeleccionada", categoria);
        model.addAttribute("events", events);
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
        List<Category> categoriasSeleccionadas =
                categoryService.findAllById(categories);

        Integer userId = 1; // CAMBIAR luego por usuario logueado real

        eventService.save(event, categoriasSeleccionadas, userId);

        return "redirect:/eventos";
    }

    // OBTENER EVENTO POR ID (para modal)
    @GetMapping("/{id}")
    @ResponseBody
    public EventResponseDTO obtenerEvento(@PathVariable Integer id) {
        return eventService.findById(id);
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

        Integer userId = 1; // ⚠ CAMBIAR por usuario logueado real

        return eventService.findByUserIdForCalendar(userId);
    }
}