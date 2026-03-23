package com.website.main.service;

import com.website.main.model.Preference;
import com.website.main.dto.Preference.PreferenceResponseDTO;
import com.website.main.dto.Preference.PreferenceUpdateDTO;
import com.website.main.mapper.PreferenceMapper;
import com.website.main.repository.PreferenceRepository;

import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PreferenceService {

    private final PreferenceRepository preferenceRepository;
    private final PreferenceMapper preferenceMapper;

    public PreferenceService(PreferenceRepository preferenceRepository, 
            PreferenceMapper preferenceMapper) {
        this.preferenceRepository = preferenceRepository;
        this.preferenceMapper = preferenceMapper;
    }

    public PreferenceResponseDTO getOrCreateByUserId(Integer userId) {
        Optional<Preference> opt = preferenceRepository.findByUserId(userId);
        if (opt.isPresent()) return preferenceMapper.toDTO(opt.get());

        Preference p = new Preference(userId);
        // por defecto, el nivel de tamaño de texto y botones es 1, y los colores del menú son 0
        p.setTextSizeLevel(1);
        p.setButtonSizeLevel(1);
        p.setMenuMainColor(0);
        p.setMenuSecondaryColor(0);
        return preferenceMapper.toDTO(preferenceRepository.save(p));
    }

    public PreferenceResponseDTO update(Integer userId, PreferenceUpdateDTO preferenceDTO) {
        
        Preference pref = preferenceRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Preferencias no encontradas para el usuario " + userId));

        if(preferenceDTO.getTextSizeLevel() != null) pref.setTextSizeLevel(preferenceDTO.getTextSizeLevel());
        if(preferenceDTO.getButtonSizeLevel() != null) pref.setButtonSizeLevel(preferenceDTO.getButtonSizeLevel());
        if(preferenceDTO.getMenuMainColor() != null) pref.setMenuMainColor(preferenceDTO.getMenuMainColor());
        if(preferenceDTO.getMenuSecondaryColor() != null) pref.setMenuSecondaryColor(preferenceDTO.getMenuSecondaryColor());
        
        return preferenceMapper.toDTO(preferenceRepository.save(pref));
    }
}
