package com.website.main.controller;

import com.website.main.dto.ActividadMadrid.ActividadMadridDTO;
import com.website.main.service.ActividadMadridService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/actividades-madrid")
public class ActividadMadridController {

    private final ActividadMadridService actividadMadridService;

    public ActividadMadridController(ActividadMadridService actividadMadridService) {
        this.actividadMadridService = actividadMadridService;
    }

    @GetMapping
    public String actividades(@RequestParam(required = false) String distrito,
                              @RequestParam(required = false) String precio,
                              Model model) {

        List<ActividadMadridDTO> actividades = actividadMadridService.obtenerActividades();

        // extraer distritos únicos para el filtro
        List<String> distritos = actividades.stream()
                .map(ActividadMadridDTO::getDistrito)
                .filter(d -> d != null && !d.isBlank())
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        // aplicar filtros si se pasan
        if (distrito != null && !distrito.isBlank()) {
            actividades = actividades.stream()
                    .filter(a -> distrito.equals(a.getDistrito()))
                    .collect(Collectors.toList());
        }

        if ("gratuita".equals(precio)) {
            actividades = actividades.stream()
                    .filter(ActividadMadridDTO::isGratuita)
                    .collect(Collectors.toList());
        } else if ("precio".equals(precio)) {
            actividades = actividades.stream()
                    .filter(a -> !a.isGratuita())
                    .collect(Collectors.toList());
        }

        model.addAttribute("actividades", actividades);
        model.addAttribute("distritos", distritos);
        model.addAttribute("distritoSeleccionado", distrito);
        model.addAttribute("precioSeleccionado", precio);
        model.addAttribute("currentPage", "actividades-madrid");

        return "actividades-madrid";
    }
}