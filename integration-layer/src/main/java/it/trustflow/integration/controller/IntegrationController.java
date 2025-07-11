package it.trustflow.integration.controller;

import it.trustflow.integration.service.IntegrationService;
import it.trustflow.integration.service.Oauth2TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/integration")
public class IntegrationController {

    @Autowired
    private IntegrationService integrationService;

    @Autowired
    private Oauth2TestService oauth2TestService;

    @GetMapping
    public String testOauth() {
        return oauth2TestService.callProtectedEndpoint();
    }

    @PostMapping("/firma-digitale")
    public String firmaDigitale() {
        return integrationService.firmaDocumento("");
    }

    @PostMapping("/invio-documento")
    public String invioDocumento() {
        return integrationService.invioDocumento("");
    }
}
