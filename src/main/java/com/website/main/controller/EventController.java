package com.website.main.controller;

import java.time.Year;
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

    @PostMapping("/crear")
    public String crearEvento(@ModelAttribute Event event) {
        System.out.println("Evento recibido: " + event.getTitle() + ", fechas: " + event.getStartDate() + " - " + event.getEndDate());
        eventService.save(event, 1); // POR AHORA ES 1 PORQUE NO TENEMOS EL LOGIN IMPLEMENTADO, PERO DEBERÍA SER EL ID DEL USUARIO LOGUEADO
        return "redirect:/eventos";
    }

    @GetMapping("/{id}")
    @ResponseBody
    public Event obtenerEvento(@PathVariable Integer id) {
        return eventService.findById(id);
    }
}