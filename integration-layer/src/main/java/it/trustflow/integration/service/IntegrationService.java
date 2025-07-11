package it.trustflow.integration.service;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class IntegrationService {
    private static final String FIRMA_DIGITALE_ENDPOINT = "http://localhost:9000/api/external-services/firma-digitale";
    private static final String INVIO_DOCUMENTO_ENDPOINT = "http://localhost:9000/api/external-services/invio-documento";

    public String firmaDocumento(String document) {
        // Chiamo il, servizio di firma digitale
        return callPostEndpoint(FIRMA_DIGITALE_ENDPOINT, document);
    }

    public String invioDocumento(String document) {
        // Chiamo il servizio di invio documento
        return callPostEndpoint(INVIO_DOCUMENTO_ENDPOINT, document);
    }

    private String callPostEndpoint(String endpoint, Object requestBody) {
        // Richiesta token da sé stesso
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth("demo-client", "secret");
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "client_credentials");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<Map> tokenResponse = restTemplate.postForEntity(
            "http://localhost:9000/oauth2/token",
            request,
            Map.class
        );

        String token = (String) tokenResponse.getBody().get("access_token");

        // Chiamata all’endpoint esterno
        HttpHeaders authHeaders = new HttpHeaders();
        authHeaders.setBearerAuth(token);

        HttpEntity<Object> authRequest = new HttpEntity<>(requestBody, authHeaders);

        ResponseEntity<String> response = restTemplate.postForEntity(
            endpoint,
            authRequest,
            String.class
        );

        return response.getBody();
    }
}
