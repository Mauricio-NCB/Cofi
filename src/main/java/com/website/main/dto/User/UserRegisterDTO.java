package com.website.main.dto.User;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRegisterDTO {
    
    private String name;
    private String lastname;
    private String password;
    private String postcode;
}
