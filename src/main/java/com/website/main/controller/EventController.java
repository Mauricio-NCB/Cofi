package com.website.main.controller;

import java.time.Year;

import com.website.main.model.Event;
import com.website.main.repository.EventRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class EventController {

    private final EventRepository eventRepository;

    public EventController(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @GetMapping("/eventos")
    public String eventos(Model model) {

        model.addAttribute("siteTitle", "Mi sitio");
        model.addAttribute("userName", "Mauricio");
        model.addAttribute("year", Year.now().getValue());

        Iterable<Event> events = eventRepository.findAll();
        model.addAttribute("events", events);

        return "events";
    }
}
