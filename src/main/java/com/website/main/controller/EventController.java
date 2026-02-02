package com.website.main.controller;

import java.time.Year;
import java.util.List;

import com.website.main.model.Event;
import com.website.main.service.EventService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping("/eventos")
    public String eventos(Model model) {

        model.addAttribute("siteTitle", "Mi sitio");
        model.addAttribute("userName", "Mauricio");
        model.addAttribute("year", Year.now().getValue());

        List<Event> events = eventService.findAll();
        model.addAttribute("events", events);

        return "events";
    }
}
