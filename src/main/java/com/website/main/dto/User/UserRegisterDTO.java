package com.website.main.dto.User;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserRegisterDTO {
    
    private String name;
    private String lastname;
    private String password;
    private String postcode;
    private List<String> categoryNames;
}
