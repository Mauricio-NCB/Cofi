package com.website.main.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.website.main.dto.User.AuthResponseDTO;
import com.website.main.dto.User.UserLoginDTO;
import com.website.main.dto.User.UserParticipantDTO;
import com.website.main.dto.User.UserRegisterDTO;
import com.website.main.dto.User.UserResponseDTO;
import com.website.main.service.UserService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
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
    @PostMapping("/check-user")
    public boolean checkUser(@RequestBody UserParticipantDTO participantDTO) {
        return userService.userExists(participantDTO.getName(), participantDTO.getLastName());
    }

    @ResponseBody
    @PostMapping("/refresh")
    public AuthResponseDTO refresh(@RequestParam String refreshToken) {
        
        return userService.refreshToken(refreshToken);
    }

    @ResponseBody
    @PostMapping("/update-categories")
    public void updateUserCategories(@RequestBody List<String> categoryNames) {

        Integer userId = (Integer) SecurityContextHolder
            .getContext()
            .getAuthentication()
            .getPrincipal();

        userService.updateUserCategories(userId, categoryNames);
    }

    @ResponseBody
    @PutMapping("/update-postcode")
    public UserResponseDTO updatePostCode(@RequestBody Map<String, String> body) {

        Integer userId = (Integer) SecurityContextHolder
            .getContext()
            .getAuthentication()
            .getPrincipal();

        return userService.updatePostCode(userId, body.get("postcode"));
    }

    @ResponseBody
    @PostMapping("/logout")
    public void logout(@RequestParam String refreshToken) {
        
        userService.logout(refreshToken);
    }
}
