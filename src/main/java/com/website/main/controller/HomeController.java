package com.website.main.controller;

import java.util.Arrays;
import java.util.List;
import java.time.Year;

import com.website.main.model.Activity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String root() {
        return "index";
    }

    @GetMapping("/eventos")
    public String eventos(Model model) {
        model.addAttribute("siteTitle", "Mi sitio");
        model.addAttribute("userName", "Mauricio");
        model.addAttribute("year", Year.now().getValue());

        List<Activity> activities = Arrays.asList(
            new Activity("Salida a la sierra", "Mauricio Calderón", "Senderismo", "https://picsum.photos/300/200?1"),
            new Activity("Clases de Yoga", "Lucía Pérez", "Yoga", "https://picsum.photos/300/200?2"),
            new Activity("Cine al aire libre", "Eduardo Gómez", "Cine", "https://picsum.photos/300/200?3"),
            new Activity("Visita cultural", "Ana Ruiz", "Cultura", "https://picsum.photos/300/200?4")
        );

        model.addAttribute("activities", activities);
        return "eventos";
    }

    @GetMapping("/ping")
    @org.springframework.web.bind.annotation.ResponseBody
    public String ping() {
        return "pong";
    }
}