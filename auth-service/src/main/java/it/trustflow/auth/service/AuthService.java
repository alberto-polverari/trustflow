package it.trustflow.auth.service;

import it.trustflow.auth.dto.AuthRequest;
import it.trustflow.auth.dto.AuthResponse;
import it.trustflow.auth.entity.Utente;
import it.trustflow.auth.repository.UserRepository;
import it.trustflow.auth.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtil jwtUtil;

    public AuthResponse login(AuthRequest authRequest) {
        Utente utente = userRepository.findByUsername(authRequest.getUsername())
            .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(authRequest.getPassword(), utente.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        String token = jwtUtil.generateToken(utente.getUsername(), utente.getTenant().getId(), utente.getRole());
        return new AuthResponse(token);
    }
}

