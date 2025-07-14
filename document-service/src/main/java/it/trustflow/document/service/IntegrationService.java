package it.trustflow.document.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@Service
public class IntegrationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(IntegrationService.class);

    @Value("${integration.client-id}")
    private String clientId;

    @Value("${integration.client-secret}")
    private String clientSecret;

    @Value("${integration.oauth-token-url}")
    private String tokenUrl;

    @Value("${integration.firma-digitale}")
    private String firmaDigitaleEndpoint;

    @Value("${integration.invio-documento}")
    private String invioDocumentoEndpoint;

    @Autowired
    private AuditLogService auditLogService;

    public String firmaDocumento(String document) {
        // Chiamo il, servizio di firma digitale
        return callPostEndpoint(firmaDigitaleEndpoint, document);
    }

    public String invioDocumento(String document) {
        // Chiamo il servizio di invio documento
        return callPostEndpoint(invioDocumentoEndpoint, document);
    }

    private String callPostEndpoint(String endpoint, Object requestBody) {
        // Richiesta token da sé stesso
        RestTemplate restTemplate = new RestTemplate();
        LOGGER.info("Inizio chiamata al servizio: {}", endpoint);

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(clientId, clientSecret);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "client_credentials");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        LOGGER.info("Richiesta token jwt: {}", tokenUrl);
        UriComponentsBuilder uriTokenBuilder = UriComponentsBuilder.fromHttpUrl(tokenUrl);
        ResponseEntity<Map> tokenResponse = restTemplate.postForEntity(
            uriTokenBuilder.build().toUri(),
            request,
            Map.class
        );
        String token = (String) tokenResponse.getBody().get("access_token");
        LOGGER.info("Token ottenuto: {}", token);

        // Chiamata all’endpoint esterno
        HttpHeaders authHeaders = new HttpHeaders();
        authHeaders.setBearerAuth(token);

        HttpEntity<Object> authRequest = new HttpEntity<>(requestBody, authHeaders);

        LOGGER.info("Esecuzione POST su endpoint: {}", endpoint);
        UriComponentsBuilder uriEndpointBuilder = UriComponentsBuilder.fromHttpUrl(endpoint);
        ResponseEntity<String> response = restTemplate.postForEntity(
            uriEndpointBuilder.build().toUri(),
            authRequest,
            String.class
        );
        LOGGER.info("Chiamata eseguita correttamente: {}", response.getBody());

        return response.getBody();
    }
}
