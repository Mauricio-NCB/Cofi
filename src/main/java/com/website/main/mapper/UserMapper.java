package com.website.main.mapper;

import org.springframework.stereotype.Component;

import com.website.main.dto.User.UserResponseDTO;
import com.website.main.model.User;

@Component
public class UserMapper {
    
    public UserResponseDTO toDTO(User userDTO) {

        if (userDTO == null) return null;

        UserResponseDTO userResponseDTO = new UserResponseDTO();
        userResponseDTO.setId(userDTO.getId());
        userResponseDTO.setName(userDTO.getName());
        userResponseDTO.setLastname(userDTO.getLastname());
        userResponseDTO.setPostcode(userDTO.getPostcode());
        return userResponseDTO;
    }
}
