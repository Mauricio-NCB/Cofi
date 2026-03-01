package com.website.main.service;

import com.website.main.model.Preference;
import com.website.main.repository.PreferenceRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PreferenceService {

    private final PreferenceRepository repo;

    public PreferenceService(PreferenceRepository repo) {
        this.repo = repo;
    }

    public Preference getOrCreateByUserId(Integer userId) {
        Optional<Preference> opt = repo.findByUserId(userId);
        if (opt.isPresent()) return opt.get();

        Preference p = new Preference(userId);
        // por defecto, el nivel de tamaño de texto y botones es 1, y los colores del menú son 0
        p.setTextSizeLevel(1);
        p.setButtonSizeLevel(1);
        p.setMenuMainColor(0);
        p.setMenuSecondaryColor(0);
        return repo.save(p);
    }

    public Preference save(Preference p) {
        return repo.save(p);
    }
}
