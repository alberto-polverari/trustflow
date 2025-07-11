package it.trustflow.integration.controller;

import it.trustflow.integration.service.Oauth2TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestOauthController {

    @Autowired
    private Oauth2TestService oauth2TestService;

    @GetMapping
    public String testOauth() {
        return oauth2TestService.callProtectedEndpoint();
    }
}
