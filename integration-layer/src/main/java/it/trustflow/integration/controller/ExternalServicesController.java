package it.trustflow.integration.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/external-services")
public class ExternalServicesController {

    @GetMapping
    public String get() {
        return "Accesso autorizzato";
    }

    @PostMapping("/firma-digitale")
    public String firmaDigitale() {
        return "Documento firmato digitalmente";
    }

    @PostMapping("/invio-documento")
    public String invioDocumento() {
        return "Documento inviato con successo";
    }
}
