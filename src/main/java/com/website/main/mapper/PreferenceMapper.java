package com.website.main.mapper;

import org.springframework.stereotype.Component;

import com.website.main.dto.Preference.PreferenceResponseDTO;
import com.website.main.model.Preference;

@Component
public class PreferenceMapper {
    
    public PreferenceResponseDTO toDTO(Preference preference) {

        if (preference == null) return null;

        PreferenceResponseDTO responseDTO = new PreferenceResponseDTO();
        responseDTO.setId(preference.getId());
        responseDTO.setTextSizeLevel(preference.getTextSizeLevel());
        responseDTO.setButtonSizeLevel(preference.getButtonSizeLevel());
        responseDTO.setMenuMainColor(preference.getMenuMainColor());
        responseDTO.setMenuSecondaryColor(preference.getMenuSecondaryColor());

        return responseDTO;
    }
}
