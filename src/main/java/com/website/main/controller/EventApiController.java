package com.website.main.controller;

import com.website.main.model.Event;
import com.website.main.repository.EventRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventApiController {

    private final EventRepository repo;

    public EventApiController(EventRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<Event> all() {
        return repo.findAll();
    }
}
