package com.website.main.controller;

import java.time.Year;

import com.website.main.model.Event;
import com.website.main.repository.EventRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final EventRepository eventRepository;

    public HomeController(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @GetMapping("/")
    public String root() {
        return "index";
    }

    @GetMapping("/eventos")
    public String eventos(Model model) {
        model.addAttribute("siteTitle", "Mi sitio");
        model.addAttribute("userName", "Mauricio");
        model.addAttribute("year", Year.now().getValue());

        Iterable<Event> events = eventRepository.findAll();
        model.addAttribute("events", events);
        return "eventos";
    }

    @GetMapping("/ping")
    @org.springframework.web.bind.annotation.ResponseBody
    public String ping() {
        return "pong";
    }
}