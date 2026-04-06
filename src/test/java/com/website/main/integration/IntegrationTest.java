package com.website.main.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.website.main.dto.User.UserLoginDTO;
import com.website.main.dto.User.UserRegisterDTO;
import com.website.main.model.User;
import com.website.main.model.Event;
import com.website.main.model.Category;
import com.website.main.repository.UserRepository;
import com.website.main.repository.EventRepository;
import com.website.main.repository.CategoryRepository;
import com.website.main.security.JwtService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// @SpringBootTest levanta el contexto completo de Spring
// @AutoConfigureMockMvc configura MockMvc para simular peticiones HTTP
// @ActiveProfiles usa application-test.properties con H2
// @Transactional hace rollback después de cada test para mantener BD limpia
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("Tests de integración")
class IntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UserRepository userRepository;
    @Autowired private EventRepository eventRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private JwtService jwtService;

    private User testUser;
    private Event testEvent;
    private String accessToken;

    @BeforeEach
    void setUp() {
        // crear usuario de prueba directamente en BD
        testUser = new User();
        testUser.setName("Juan");
        testUser.setLastname("García");
        testUser.setPassword(passwordEncoder.encode("123"));
        testUser.setPostcode("28001");
        testUser.setRolAdmin(0);
        testUser.setState("Activo");
        testUser.setNotified(false);
        testUser.setVerified(false);
        testUser = userRepository.save(testUser);

        // crear categoría de prueba
        Category testCategory = new Category("Ocio");
        testCategory = categoryRepository.save(testCategory);

        // crear evento de prueba
        testEvent = new Event();
        testEvent.setTitle("Evento de Prueba");
        testEvent.setDescription("Este es un evento de prueba");
        testEvent.setDateEvent(LocalDate.now().plusDays(5));
        testEvent.setTimeEvent(LocalTime.of(10, 30));
        testEvent.setMaxCapacity(50);
        testEvent.setPostcode("28001");
        testEvent.setState("proximo");
        testEvent.setUser(testUser);
        testEvent.setCategories(List.of(testCategory));
        testEvent = eventRepository.save(testEvent);

        // generar token válido para tests que requieren autenticación
        accessToken = jwtService.generateAccessToken(testUser.getId());
    }

    // =====================================================
    @Nested
    @DisplayName("Registro de usuario")
    class Register {
    // =====================================================

        @Test
        @DisplayName("Debería registrar un usuario nuevo correctamente")
        void usuarioNuevo_deberiaRegistrarseCorrectamente() throws Exception {
            UserRegisterDTO dto = new UserRegisterDTO();
            dto.setName("María");
            dto.setLastname("López");
            dto.setPassword("123");
            dto.setPostcode("28002");

            mockMvc.perform(post("/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("María"))
                    .andExpect(jsonPath("$.lastname").value("López"))
                    .andExpect(jsonPath("$.password").doesNotExist());
        }

        @Test
        @DisplayName("Debería devolver error si el usuario ya existe")
        void usuarioDuplicado_deberiaLanzarError() throws Exception {
            // Juan García ya existe en setUp
            UserRegisterDTO dto = new UserRegisterDTO();
            dto.setName("Juan");
            dto.setLastname("García");
            dto.setPassword("123");
            dto.setPostcode("28001");

            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                try {
                    mockMvc.perform(post("/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            
            assertTrue(exception.getMessage().contains("El usuario ya existe"));
        }

        @Test
        @DisplayName("No debería devolver la contraseña en la respuesta")
        void registro_noDeberiaExponerPassword() throws Exception {
            UserRegisterDTO dto = new UserRegisterDTO();
            dto.setName("Pedro");
            dto.setLastname("Martínez");
            dto.setPassword("123");
            dto.setPostcode("28003");

            mockMvc.perform(post("/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.password").doesNotExist())
                    .andExpect(jsonPath("$.refreshToken").doesNotExist());
        }
    }

    // =====================================================
    @Nested
    @DisplayName("Login de usuario")
    class Login {
    // =====================================================

        @Test
        @DisplayName("Debería devolver tokens si las credenciales son correctas")
        void credencialesCorrectas_deberiaRetornarTokens() throws Exception {
            UserLoginDTO dto = new UserLoginDTO();
            dto.setName("Juan");
            dto.setLastname("García");
            dto.setPassword("123");

            mockMvc.perform(post("/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.accessToken").isNotEmpty())
                    .andExpect(jsonPath("$.refreshToken").isNotEmpty())
                    .andExpect(jsonPath("$.user.name").value("Juan"))
                    .andExpect(jsonPath("$.user.lastname").value("García"))
                    .andExpect(jsonPath("$.user.password").doesNotExist());
        }

        @Test
        @DisplayName("Debería devolver error si el usuario no existe")
        void usuarioNoExiste_deberiaLanzarError() throws Exception {
            UserLoginDTO dto = new UserLoginDTO();
            dto.setName("UsuarioQueNoExiste");
            dto.setLastname("Apellido");
            dto.setPassword("123");

            Exception exception = assertThrows(Exception.class, () -> {
                try {
                    mockMvc.perform(post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            
            assertNotNull(exception);
        }

        @Test
        @DisplayName("Debería devolver error si la contraseña es incorrecta")
        void contrasenaIncorrecta_deberiaLanzarError() throws Exception {
            UserLoginDTO dto = new UserLoginDTO();
            dto.setName("Juan");
            dto.setLastname("García");
            dto.setPassword("contrasenaIncorrecta");

            Exception exception = assertThrows(Exception.class, () -> {
                try {
                    mockMvc.perform(post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            
            assertNotNull(exception);
        }
    }

    // =====================================================
    @Nested
    @DisplayName("Seguridad y acceso")
    class Security {
    // =====================================================

        @Test
        @DisplayName("Debería permitir acceso a /eventos sin autenticación")
        void eventos_deberiaSerAccesibleSinToken() throws Exception {
            mockMvc.perform(get("/eventos"))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Debería bloquear acceso a /chat sin autenticación")
        void chat_deberiaBloquearseSinToken() throws Exception {
            mockMvc.perform(get("/chat"))
                    .andExpect(status().is3xxRedirection());
        }

        @Test
        @DisplayName("Debería bloquear acceso a /panel sin autenticación")
        void panel_deberiaBloquearseSinToken() throws Exception {
            mockMvc.perform(get("/panel"))
                    .andExpect(status().is3xxRedirection());
        }

        @Test
        @DisplayName("Debería permitir acceso a /chat con token válido")
        void chat_deberiaPermitirConTokenValido() throws Exception {
            mockMvc.perform(get("/chat")
                    .header("Authorization", "Bearer " + accessToken))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Debería bloquear acceso con token inválido")
        void tokenInvalido_deberiaBloquear() throws Exception {
            mockMvc.perform(get("/chat")
                    .header("Authorization", "Bearer tokeninvalido"))
                    .andExpect(status().is3xxRedirection());
        }
    }

    // =====================================================
    @Nested
    @DisplayName("Eventos")
    class Eventos {
    // =====================================================

        @Test
        @DisplayName("Debería obtener un evento por id")
        void obtenerEventoPorId_deberiaRetornarEvento() throws Exception {
            mockMvc.perform(get("/eventos/" + testEvent.getId())
                    .header("Authorization", "Bearer " + accessToken))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Debería requerir autenticación para unirse a un evento")
        void unirseAlEvento_sinToken_deberiaRedirigirAlLogin() throws Exception {
            mockMvc.perform(post("/eventos/" + testEvent.getId() + "/unirse"))
                    .andExpect(status().is3xxRedirection());
        }

        @Test
        @DisplayName("Debería requerir autenticación para salir de un evento")
        void salirDelEvento_sinToken_deberiaRedirigirAlLogin() throws Exception {
            mockMvc.perform(post("/eventos/" + testEvent.getId() + "/salir"))
                    .andExpect(status().is3xxRedirection());
        }

        @Test
        @DisplayName("Debería retornar lista de eventos del calendario con token")
        void misEventos_conToken_deberiaRetornarLista() throws Exception {
            mockMvc.perform(get("/eventos/api/mis-eventos")
                    .header("Authorization", "Bearer " + accessToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray());
        }
    }

    // =====================================================
    @Nested
    @DisplayName("Logout y refresh token")
    class LogoutAndRefresh {
    // =====================================================

        @Test
        @DisplayName("Debería hacer logout correctamente")
        void logout_deberiaEliminarRefreshToken() throws Exception {
            // primero hacer login para obtener refresh token
            UserLoginDTO loginDTO = new UserLoginDTO();
            loginDTO.setName("Juan");
            loginDTO.setLastname("García");
            loginDTO.setPassword("123");

            MvcResult loginResult = mockMvc.perform(post("/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(loginDTO)))
                    .andExpect(status().isOk())
                    .andReturn();

            String responseBody = loginResult.getResponse().getContentAsString();
            String refreshToken = objectMapper.readTree(responseBody)
                    .get("refreshToken").asText();

            // hacer logout con el refresh token
            mockMvc.perform(post("/auth/logout")
                    .param("refreshToken", refreshToken)
                    .header("Authorization", "Bearer " + accessToken))
                    .andExpect(status().isOk());

            // verificar que el refresh token se eliminó de BD
            User updatedUser = userRepository.findById(testUser.getId()).orElseThrow();
            assertNull(updatedUser.getRefreshToken());
        }

        @Test
        @DisplayName("Debería renovar el access token con refresh token válido")
        void refresh_conTokenValido_deberiaRetornarNuevoToken() throws Exception {
            // hacer login para obtener refresh token
            UserLoginDTO loginDTO = new UserLoginDTO();
            loginDTO.setName("Juan");
            loginDTO.setLastname("García");
            loginDTO.setPassword("123");

            MvcResult loginResult = mockMvc.perform(post("/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(loginDTO)))
                    .andExpect(status().isOk())
                    .andReturn();

            String responseBody = loginResult.getResponse().getContentAsString();
            String refreshToken = objectMapper.readTree(responseBody)
                    .get("refreshToken").asText();

            // usar el refresh token para obtener nuevo access token
            mockMvc.perform(post("/auth/refresh")
                    .param("refreshToken", refreshToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.accessToken").isNotEmpty())
                    .andExpect(jsonPath("$.refreshToken").isNotEmpty());
        }

        @Test
        @DisplayName("Debería lanzar error con refresh token inválido")
        void refresh_conTokenInvalido_deberiaLanzarError() throws Exception {
            Exception exception = assertThrows(Exception.class, () -> {
                try {
                    mockMvc.perform(post("/auth/refresh")
                            .param("refreshToken", "tokenInvalido"));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            
            assertNotNull(exception);
        }
    }
}