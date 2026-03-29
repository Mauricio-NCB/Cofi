package com.website.main.dto.User;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthResponseDTO {
    
    private String accessToken;
    private String refreshToken;
    private UserResponseDTO user;
}
