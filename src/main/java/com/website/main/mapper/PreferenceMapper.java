package com.website.main.mapper;

import com.website.main.dto.Preference.PreferenceResponseDTO;
import com.website.main.model.Preference;

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
