package com.website.main.controller;

import java.time.Year;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import com.website.main.model.Category;
import com.website.main.model.Event;
import com.website.main.service.CategoryService;
import com.website.main.service.EventService;

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

        List<Event> events;

        if (categoria != null) {
            events = eventService.findByCategoryId(categoria);
        } else {
            events = eventService.findAll();
        }
        
        model.addAttribute("categoriaSeleccionada", categoria);
        model.addAttribute("events", events);
        model.addAttribute("categorias", categoryService.findAll());
        model.addAttribute("event", new Event());

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
        model.addAttribute("event", new Event());

        return "crear-evento";
    }

    // CREAR EVENTO (ManyToMany categorías)
    @PostMapping("/crear")
    public String crearEvento(@ModelAttribute Event event,
                              @RequestParam List<Integer> categories) {

        // Convertimos IDs en entidades Category
        List<Category> categoriasSeleccionadas =
                categoryService.findAllById(categories);

        event.setCategories(categoriasSeleccionadas);

        Integer userId = 1; // CAMBIAR luego por usuario logueado real

        eventService.save(event, userId);

        return "redirect:/eventos";
    }

    // OBTENER EVENTO POR ID (para modal)
    @GetMapping("/{id}")
    @ResponseBody
    public Event obtenerEvento(@PathVariable Integer id) {
        return eventService.findById(id);
    }

    // CALENDARIO
    @GetMapping("/calendario")
    public String calendario(Model model) {
        model.addAttribute("currentPage", "calendario");
        return "calendario";
    }

    // FULLCALENDAR
    @GetMapping("/api/mis-eventos")
    @ResponseBody
    public List<Map<String, Object>> misEventos() {

        Integer userId = 1; // ⚠ CAMBIAR por usuario logueado real

        List<Event> eventos = eventService.findByUserId(userId);

        return eventos.stream().map(ev -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", ev.getId());
            map.put("title", ev.getTitle());
            map.put("start", ev.getDateEvent().toString() + "T" + ev.getTimeEvent().toString());
            map.put("description", ev.getDescription());
            map.put("dateEvent", ev.getDateEvent().toString());
            map.put("timeEvent", ev.getTimeEvent().toString());
            map.put("maxCapacity", ev.getMaxCapacity());
            map.put("estado", ev.getState());
            return map;
        }).toList();
    }
}