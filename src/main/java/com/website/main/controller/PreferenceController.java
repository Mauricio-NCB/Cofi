package com.website.main.controller;

import com.website.main.dto.Preference.PreferenceResponseDTO;
import com.website.main.dto.Preference.PreferenceUpdateDTO;
import com.website.main.service.PreferenceService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/preferences")
public class PreferenceController {

    private final PreferenceService service;

    public PreferenceController(PreferenceService service) {
        this.service = service;
    }

    @GetMapping
    public PreferenceResponseDTO get(@RequestParam(required = false) Integer userId) {
        if (userId == null) userId = 1;
        return service.getOrCreateByUserId(userId);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public PreferenceResponseDTO update(@RequestParam(required = false) Integer userId, @RequestBody PreferenceUpdateDTO preferenceDTO) {
        
        if (userId == null) userId = 1;

        return service.update(userId, preferenceDTO);
    }
}
