package com.website.main.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.website.main.security.JwtService;

import com.website.main.model.User;
import com.website.main.dto.User.AuthResponseDTO;
import com.website.main.dto.User.UserLoginDTO;
import com.website.main.dto.User.UserRegisterDTO;
import com.website.main.dto.User.UserResponseDTO;
import com.website.main.mapper.UserMapper;
import com.website.main.repository.UserRepository;

@Service
public class UserService {
    
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UserService(UserRepository userRepository, UserMapper userMapper, 
            PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public UserResponseDTO register(UserRegisterDTO userRegisterDTO) {
        User user = new User();

        // Refresh token se genera al hacer login, no al registrarse
        user.setName(userRegisterDTO.getName());
        user.setLastname(userRegisterDTO.getLastname());
        user.setPassword(passwordEncoder.encode(userRegisterDTO.getPassword()));
        user.setPostcode(userRegisterDTO.getPostcode());
        user.setRolAdmin(0); // 0 = usuario normal
        user.setState("Activo");
        user.setNotified(false);
        user.setVerified(false);


        User registeredUser = userRepository.save(user);

        return userMapper.toDTO(registeredUser);
    }

    public AuthResponseDTO login(UserLoginDTO userLoginDTO) {
        User user = userRepository.findByNameAndLastname(userLoginDTO.getName(), userLoginDTO.getLastname())
                            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!passwordEncoder.matches(userLoginDTO.getPassword(), user.getPassword())) {
            throw new RuntimeException("Credenciales inválidas");
        }

        String accessToken = jwtService.generateAccessToken(user.getId());
        String refreshToken = jwtService.generateRefreshToken(user.getId());

        user.setRefreshToken(refreshToken);        
        userRepository.save(user);

        AuthResponseDTO authResponseDTO = new AuthResponseDTO();
        authResponseDTO.setAccessToken(accessToken);
        authResponseDTO.setRefreshToken(refreshToken);
        authResponseDTO.setUser(userMapper.toDTO(user));

        return authResponseDTO;
    }

    public AuthResponseDTO refreshToken(String refreshToken) {
        User user = userRepository.findByRefreshToken(refreshToken)
                    .orElseThrow(() -> new RuntimeException("Refresh token inválido"));

        String newAccessToken = jwtService.generateAccessToken(user.getId());
        String newRefreshToken = jwtService.generateRefreshToken(user.getId());

        user.setRefreshToken(newRefreshToken);
        userRepository.save(user);

        AuthResponseDTO authResponseDTO = new AuthResponseDTO();
        authResponseDTO.setAccessToken(newAccessToken);
        authResponseDTO.setRefreshToken(newRefreshToken);
        authResponseDTO.setUser(userMapper.toDTO(user));

        return authResponseDTO;
    }

    public void logout(String refreshToken) {
        User user = userRepository.findByRefreshToken(refreshToken)
                    .orElseThrow(() -> new RuntimeException("Token inválido"));

        user.setRefreshToken(null);
        userRepository.save(user);
    }


}
