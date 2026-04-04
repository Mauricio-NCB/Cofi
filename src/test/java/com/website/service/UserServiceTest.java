package com.website.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.website.main.dto.User.AuthResponseDTO;
import com.website.main.dto.User.UserLoginDTO;
import com.website.main.dto.User.UserRegisterDTO;
import com.website.main.dto.User.UserResponseDTO;
import com.website.main.mapper.UserMapper;
import com.website.main.model.Category;
import com.website.main.model.User;
import com.website.main.repository.CategoryRepository;
import com.website.main.repository.UserRepository;
import com.website.main.security.JwtService;
import com.website.main.service.UserService;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Test")
class UserServiceTest {

    // Mocks de dependencias
    @Mock private UserRepository userRepository;
    @Mock private CategoryRepository categoryRepository;
    @Mock private UserMapper userMapper;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtService jwtService;

    // Servicio a probar
    @InjectMocks private UserService userService;

    // Datos de prueba temporales
    private UserRegisterDTO registeredUserDTO;
    private UserLoginDTO loginUserDTO;
    private UserResponseDTO expectedUserResponseDTO;
    private User user;

    @BeforeEach
    void setUp() {
        // Inicializar datos de prueba
        registeredUserDTO = new UserRegisterDTO();
        registeredUserDTO.setName("John");
        registeredUserDTO.setLastname("Doe");
        registeredUserDTO.setPassword("password123");
        registeredUserDTO.setPostcode("28001");

        loginUserDTO = new UserLoginDTO();
        loginUserDTO.setName("John");
        loginUserDTO.setLastname("Doe");
        loginUserDTO.setPassword("password123");

        user = new User();
        user.setId(1);
        user.setName("John");
        user.setLastname("Doe");
        user.setPassword("encodedPassword");
        user.setPostcode("28001");
        user.setRolAdmin(0);
        user.setState("Activo");
        user.setNotified(false);
        user.setVerified(false);

        expectedUserResponseDTO = new UserResponseDTO();
        expectedUserResponseDTO.setId(1);
        expectedUserResponseDTO.setName("John");
        expectedUserResponseDTO.setLastname("Doe");
    }

    // ----------------------------------------------------------------------------------------------

    @Nested
    @DisplayName("Registro de usuario")
    class Register {

        @Test
        @DisplayName("Debería registrar un nuevo usuario correctamente")
        void usuarioRegistradoCorrectamente() {
            // Configurar mocks
            when(userRepository.findByNameAndLastname("John", "Doe")).thenReturn(Optional.empty());
            when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
            when(userRepository.save(any(User.class))).thenReturn(user);
            when(userMapper.toDTO(any(User.class))).thenReturn(expectedUserResponseDTO);

            // Ejecutar método a probar
            UserResponseDTO actualResponse = userService.register(registeredUserDTO);

            // Verificar resultados
            assertNotNull(actualResponse);
            assertEquals(expectedUserResponseDTO.getId(), actualResponse.getId());
            assertEquals(expectedUserResponseDTO.getName(), actualResponse.getName());
            assertEquals(expectedUserResponseDTO.getLastname(), actualResponse.getLastname());

            // Verificar interacciones con mocks
            verify(userRepository, times(1)).findByNameAndLastname("John", "Doe");
            verify(passwordEncoder, times(1)).encode("password123");
            verify(userRepository, times(1)).save(any(User.class));
            verify(userMapper, times(1)).toDTO(any(User.class));
        }

        @Test
        @DisplayName("Debería lanzar excepción si el usuario ya existe")
        void usuarioYaExiste() {
            // Configurar mocks
            when(userRepository.findByNameAndLastname("John", "Doe")).thenReturn(Optional.of(user));

            // Ejecutar método a probar y verificar excepción
            RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.register(registeredUserDTO));
            assertEquals("El usuario ya existe", exception.getMessage());

            // Verificar interacciones con mocks
            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        @DisplayName("Debería lanzar excepción si la categoría no existe")
        void categoriaNoExiste() {
            // Configurar datos de prueba
            registeredUserDTO.setCategoryNames(List.of("NonExistentCategory"));

            // Configurar mocks
            when(userRepository.findByNameAndLastname("John", "Doe")).thenReturn(Optional.empty());
            when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
            when(categoryRepository.findByName("NonExistentCategory")).thenReturn(Optional.empty());

            // Ejecutar método a probar y verificar excepción
            RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.register(registeredUserDTO));
            assertEquals("Categoría NonExistentCategory no encontrada", exception.getMessage());

            // Verificar interacciones con mocks
            verify(userRepository, never()).save(any(User.class));
        }
    }

    // ----------------------------------------------------------------------------------------------

    @Nested
    @DisplayName("Login de usuario")
    class Login {

        @Test
        @DisplayName("Debería autenticar al usuario correctamente y retonar token")
        void loginCorrecto() {
            // Configurar mocks
            when(userRepository.findByNameAndLastname("John", "Doe")).thenReturn(Optional.of(user));
            when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);
            when(jwtService.generateAccessToken(1)).thenReturn("accessToken");
            when(jwtService.generateRefreshToken(1)).thenReturn("refreshToken");
            when(userRepository.save(any(User.class))).thenReturn(user);
            when(userMapper.toDTO(any(User.class))).thenReturn(expectedUserResponseDTO);

            // Ejecutar método a probar
            AuthResponseDTO authResponse = userService.login(loginUserDTO);

            // Verificar resultados
            assertNotNull(authResponse);
            assertEquals("accessToken", authResponse.getAccessToken());
            assertEquals("refreshToken", authResponse.getRefreshToken());
            assertEquals(expectedUserResponseDTO.getId(), authResponse.getUser().getId());
            assertEquals(expectedUserResponseDTO.getName(), authResponse.getUser().getName());
            assertEquals(expectedUserResponseDTO.getLastname(), authResponse.getUser().getLastname());

            // Verificar interacciones con mocks
            verify(userRepository, times(1)).findByNameAndLastname("John", "Doe");
            verify(passwordEncoder, times(1)).matches("password123", "encodedPassword");
            verify(jwtService, times(1)).generateAccessToken(1);
            verify(jwtService, times(1)).generateRefreshToken(1);
        }

        @Test
        @DisplayName("Debería lanzar excepción si el usuario no existe")
        void usuarioNoExiste() {
            // Configurar mocks
            when(userRepository.findByNameAndLastname("John", "Doe")).thenReturn(Optional.empty());

            // Ejecutar método a probar y verificar excepción
            RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.login(loginUserDTO));
            assertEquals("Usuario no encontrado", exception.getMessage());

            // Verificar interacciones con mocks
            verify(userRepository, times(1)).findByNameAndLastname("John", "Doe");
            verify(passwordEncoder, never()).matches(anyString(), anyString());
            verify(jwtService, never()).generateAccessToken(any());
            verify(jwtService, never()).generateRefreshToken(any());
        }

        @Test
        @DisplayName("Debería lanzar excepción si la contraseña es incorrecta")
        void contraseñaIncorrecta() {
            // Configurar mocks
            when(userRepository.findByNameAndLastname("John", "Doe")).thenReturn(Optional.of(user));
            when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(false);

            // Ejecutar método a probar y verificar excepción
            RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.login(loginUserDTO));
            assertEquals("Credenciales inválidas", exception.getMessage());

            // Verificar interacciones con mocks
            verify(userRepository, times(1)).findByNameAndLastname("John", "Doe");
            verify(passwordEncoder, times(1)).matches("password123", "encodedPassword");
            verify(jwtService, never()).generateAccessToken(any());
            verify(jwtService, never()).generateRefreshToken(any());
        }
    }

    // ----------------------------------------------------------------------------------------------

    @Nested
    @DisplayName("Actualización de categorías del usuario")
    class UpdateUserCategories {

        @Test
        @DisplayName("Debería actualizar las categorías del usuario correctamente")
        void updateCategoriesCorrectamente() {
            // Configurar datos de prueba
            Category ocio = new Category();
            ocio.setId(1);
            ocio.setName("Ocio");

            Category salud = new Category();
            salud.setId(2);
            salud.setName("Salud");

            List<Category> categories = List.of(ocio, salud);

            // Configurar mocks
            when(userRepository.findById(1)).thenReturn(Optional.of(user));
            when(categoryRepository.findByName("Ocio")).thenReturn(Optional.of(categories.get(0)));
            when(categoryRepository.findByName("Salud")).thenReturn(Optional.of(categories.get(1)));
            when(userRepository.save(any(User.class))).thenReturn(user);

            // Ejecutar método a probar
            userService.updateUserCategories(1, List.of(categories.get(0).getName(), categories.get(1).getName()));

            // Verificar resultados
            assertEquals(2, user.getPreferedCategories().size());
            assertEquals("Ocio", user.getPreferedCategories().get(0).getName());
            assertEquals("Salud", user.getPreferedCategories().get(1).getName());

            // Verificar interacciones con mocks
            verify(userRepository, times(1)).findById(1);
            verify(categoryRepository, times(1)).findByName("Ocio");
            verify(categoryRepository, times(1)).findByName("Salud");
            verify(userRepository, times(1)).save(any(User.class));
        }

        @Test
        @DisplayName("Debería lanzar excepción si el usuario no existe")
        void usuarioNoExiste() {
            // Configurar mocks
            when(userRepository.findById(1)).thenReturn(Optional.empty());

            // Ejecutar método a probar y verificar excepción
            RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.updateUserCategories(1, List.of("Ocio")));
            assertEquals("Usuario no encontrado", exception.getMessage());

            // Verificar interacciones con mocks
            verify(userRepository, times(1)).findById(1);
            verify(categoryRepository, never()).findByName(anyString());
            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("Debería lanzar excepción si alguna categoría no existe")
        void categoriaNoExiste() {
            // Configurar datos de prueba
            List<String> categoryNames = List.of("NonExistentCategory");

            // Configurar mocks
            when(userRepository.findById(1)).thenReturn(Optional.of(user));
            when(categoryRepository.findByName("NonExistentCategory")).thenReturn(Optional.empty());

            // Ejecutar método a probar y verificar excepción
            RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.updateUserCategories(1, categoryNames));
            assertEquals("Categoría NonExistentCategory no encontrada", exception.getMessage());

            // Verificar interacciones con mocks
            verify(userRepository, times(1)).findById(1);
            verify(categoryRepository, times(1)).findByName("NonExistentCategory");
        }
    }

    // ----------------------------------------------------------------------------------------------

    @Nested
    @DisplayName("Logout de usuario")
    class Logout {

        @Test
        @DisplayName("Debería eliminar el refresh token del usuario al hacer logout")
        void logoutCorrecto() {
            // Configurar datos de prueba
            user.setRefreshToken("refreshToken-valido");

            // Configurar mocks
            when(userRepository.findByRefreshToken("refreshToken-valido")).thenReturn(Optional.of(user));
            when(userRepository.save(any(User.class))).thenReturn(user);

            // Ejecutar método a probar
            userService.logout("refreshToken-valido");

            // Verificar resultados
            assertNull(user.getRefreshToken());

            // Verificar interacciones con mocks
            verify(userRepository, times(1)).findByRefreshToken("refreshToken-valido");
            verify(userRepository, times(1)).save(user);
        }

        @Test
        @DisplayName("Debería lanzar excepción si el token no existe")
        void tokenNoExiste() {
            // Configurar mocks
            when(userRepository.findByRefreshToken("refreshToken-invalido")).thenReturn(Optional.empty());

            // Ejecutar método a probar y verificar excepción
            RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.logout("refreshToken-invalido"));
            assertEquals("Token inválido", exception.getMessage());

            // Verificar interacciones con mocks
            verify(userRepository, times(1)).findByRefreshToken("refreshToken-invalido");
            verify(userRepository, never()).save(any());
        }
    }
}
