package it.trustflow.auth.controller;

import it.trustflow.auth.dto.AuthRequest;
import it.trustflow.auth.dto.AuthResponse;
import it.trustflow.auth.entity.Utente;
import it.trustflow.auth.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
        @RequestBody AuthRequest authRequest,
        HttpServletRequest request
    ) {
        return new ResponseEntity<AuthResponse>(authService.login(authRequest, request), HttpStatus.OK);
    }

    @GetMapping("/spid-init")
    public void spidInit(HttpServletResponse response) throws IOException {
        response.sendRedirect("/spid-login-mock.html");
    }

    @GetMapping("/spid-callback")
    public void spidCallback(
        @RequestParam("cf") String codiceFiscale,
        HttpServletRequest request,
        HttpServletResponse response
    ) throws IOException {
        String token = authService.spidLogin(codiceFiscale, request);
        response.sendRedirect("http://localhost:4200/spid/callback?token=" + token);
    }
}
