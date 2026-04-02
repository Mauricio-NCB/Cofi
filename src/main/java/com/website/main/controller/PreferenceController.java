package com.website.main.controller;

import com.website.main.dto.Preference.PreferenceResponseDTO;
import com.website.main.dto.Preference.PreferenceUpdateDTO;
import com.website.main.service.PreferenceService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/preferences")
public class PreferenceController {

    private final PreferenceService service;

    public PreferenceController(PreferenceService service) {
        this.service = service;
    }

    @GetMapping
    public PreferenceResponseDTO get() {
        Integer userId = (Integer) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
        return service.getOrCreateByUserId(userId);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public PreferenceResponseDTO update(@RequestBody PreferenceUpdateDTO preferenceDTO) {
        
        Integer userId = (Integer) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        return service.update(userId, preferenceDTO);
    }
}
