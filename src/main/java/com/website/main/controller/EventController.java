package com.website.main.controller;

import java.time.Year;
import java.util.HashMap;
import java.util.List;

import com.website.main.model.Event;
import com.website.main.service.EventService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import java.util.Map;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/eventos")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    public String eventos(Model model) {

        model.addAttribute("siteTitle", "Mi sitio");
        model.addAttribute("userName", "Mauricio");
        model.addAttribute("year", Year.now().getValue());

        List<Event> events = eventService.findAll();
        model.addAttribute("events", events);
        model.addAttribute("event", new Event());
        return "events";
    }

    @GetMapping("/crear")
    public String mostrarFormulario(Model model) {

        List<String> codigos = IntStream.rangeClosed(28001, 28055)
                .mapToObj(String::valueOf)
                .toList();

        model.addAttribute("codigosPostales", codigos);
        model.addAttribute("event", new Event());

        return "crear-evento";
    }


    @PostMapping("/crear")
    public String crearEvento(@ModelAttribute Event event) {

        Integer userId = 1; // luego desde sesión, habrá que cambiarlo para que sea el ID del usuario logueado

        eventService.save(event, userId);

        return "redirect:/eventos";
    }


    @GetMapping("/{id}")
    @ResponseBody
    public Event obtenerEvento(@PathVariable Integer id) {
        return eventService.findById(id);
    }

    @GetMapping("/calendario")
    public String calendario() {
        return "calendario";
    }

    @GetMapping("/api/mis-eventos")
    @ResponseBody
    public List<Map<String, Object>> misEventos() {

        Integer userId = 1; // CAMBIAR PARA QUE SEA EL ID DEL USUARIO LOGUEADO

        List<Event> eventos = eventService.findByUserId(userId);

        return eventos.stream().map(ev -> {
            Map<String, Object> map = new HashMap<>();
            map.put("title", ev.getTitle());
            map.put("start", ev.getStartDate());
            map.put("end", ev.getEndDate());
            map.put("estado", ev.getState());
            return map;
        }).toList();
    }
}