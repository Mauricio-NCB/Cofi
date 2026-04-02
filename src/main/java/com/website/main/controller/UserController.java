package com.website.main.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.website.main.dto.User.AuthResponseDTO;
import com.website.main.dto.User.UserLoginDTO;
import com.website.main.dto.User.UserRegisterDTO;
import com.website.main.dto.User.UserResponseDTO;
import com.website.main.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequestMapping("/auth")
public class UserController {
    
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @ResponseBody
    @PostMapping("/register")
    public UserResponseDTO register(@RequestBody UserRegisterDTO userDTO) {
        
        return userService.register(userDTO);
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @ResponseBody
    @PostMapping("/login")
    public AuthResponseDTO login(@RequestBody UserLoginDTO userDTO) {

        return userService.login(userDTO);
    }

    @ResponseBody
    @PostMapping("/refresh")
    public AuthResponseDTO refresh(@RequestParam String refreshToken) {
        
        return userService.refreshToken(refreshToken);
    }

    @ResponseBody
    @PostMapping("/logout")
    public void logout(@RequestParam String refreshToken) {
        
        userService.logout(refreshToken);
    }
}
