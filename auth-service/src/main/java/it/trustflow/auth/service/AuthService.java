package it.trustflow.auth.service;

import it.trustflow.auth.dto.AuditLog;
import it.trustflow.auth.dto.AuthRequest;
import it.trustflow.auth.dto.AuthResponse;
import it.trustflow.auth.entity.Utente;
import it.trustflow.auth.repository.UserRepository;
import it.trustflow.auth.security.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private AuditLogService auditLogService;

    public AuthResponse login(AuthRequest authRequest, HttpServletRequest request) {
        Utente utente = userRepository.findByUsername(authRequest.getUsername())
            .orElseThrow(() -> new AccessDeniedException("User not found"));

        if (!passwordEncoder.matches(authRequest.getPassword(), utente.getPassword())) {
            throw new AccessDeniedException("Invalid credentials");
        }

        String token = jwtUtil.generateToken(utente.getUsername(), utente.getTenant().getId(), utente.getRole());
        AuditLog log = AuditLog.builder()
            .tenantId(utente.getTenant().getId().toString())
            .eventType("USERNAME_LOGIN")
            .eventDescription("USERNAME Login Type")
            .eventMessage("User " + utente.getUsername() + " logged in successfully")
            .userId(utente.getId().toString())
            .build();
        auditLogService.sendAuditLog(log, request);

        return new AuthResponse(token);
    }

    public String spidLogin(String codiceFiscale, HttpServletRequest request) {
        Optional<Utente> utenteOpt = userRepository.findByCodiceFiscale(codiceFiscale);

        if (utenteOpt.isEmpty()) {
            throw new  AccessDeniedException("User not found");
        }

        Utente utente = utenteOpt.get();
        String token = jwtUtil.generateToken(utente.getUsername(), utente.getTenant().getId(), utente.getRole());

        AuditLog log = AuditLog.builder()
            .tenantId(utente.getTenant().getId().toString())
            .eventType("SPID_LOGIN")
            .eventDescription("SPID Login Type")
            .eventMessage("User " + utente.getUsername() + " logged in via SPID with codice fiscale " + codiceFiscale)
            .userId(utente.getId().toString())
            .build();
        auditLogService.sendAuditLog(log, request);

        return token;
    }
}

