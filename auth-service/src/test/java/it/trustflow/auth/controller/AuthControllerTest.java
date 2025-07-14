package it.trustflow.auth.controller;

import it.trustflow.auth.dto.AuthResponse;
import it.trustflow.auth.service.AuthService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(AuthController.class)
class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @SuppressWarnings("removal")
    @MockBean
    private AuthService authService;

    @Test
    void testLoginSuccess() throws Exception {
        AuthResponse response = new AuthResponse();
        response.setToken("token123");
        Mockito.when(authService.login(Mockito.any(), Mockito.any())).thenReturn(response);
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"admin\",\"password\":\"admin\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token123"));
    }
}
