package com.website.main.dto;

import com.website.main.dto.User.UserResponseDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthResponseDTO {
    
    private String accessToken;
    private String refreshToken;
    private UserResponseDTO user;
}
