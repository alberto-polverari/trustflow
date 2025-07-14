package it.trustflow.auth.service;

import it.trustflow.auth.dto.AuthRequest;
import it.trustflow.auth.dto.AuthResponse;
import it.trustflow.auth.entity.Utente;
import it.trustflow.auth.repository.UserRepository;
import it.trustflow.auth.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.Optional;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
class AuthServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private AuditLogService auditLogService;
    @InjectMocks
    private AuthService authService;

    @Test
    void testLoginWithInvalidUserThrowsException() {
        AuthRequest req = new AuthRequest();
        req.setUsername("notfound");
        Mockito.when(userRepository.findByUsername(any())).thenReturn(Optional.empty());
        assertThatThrownBy(() -> authService.login(req, null)).isInstanceOf(Exception.class);
    }
}
