package com.website.main.controller;

import com.website.main.model.Preference;
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
    public Preference get(@RequestParam(required = false) Integer userId) {
        if (userId == null) userId = 1; // fallback
        return service.getOrCreateByUserId(userId);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public Preference update(@RequestParam(required = false) Integer userId, @RequestBody Preference body) {
        if (userId == null) userId = body.getUserId() != null ? body.getUserId() : 1;
        Preference pref = service.getOrCreateByUserId(userId);
        if (body.getTextSizeLevel() != null) pref.setTextSizeLevel(body.getTextSizeLevel());
        if (body.getButtonSizeLevel() != null) pref.setButtonSizeLevel(body.getButtonSizeLevel());
        if (body.getMenuMainColor() != null) pref.setMenuMainColor(body.getMenuMainColor());
        if (body.getMenuSecondaryColor() != null) pref.setMenuSecondaryColor(body.getMenuSecondaryColor());
        return service.save(pref);
    }
}
