package it.trustflow.integration.service;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class Oauth2TestService {

        public String callProtectedEndpoint() {
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

            // Chiamata all’endpoint protetto
            HttpHeaders authHeaders = new HttpHeaders();
            authHeaders.setBearerAuth(token);

            HttpEntity<Void> authRequest = new HttpEntity<>(authHeaders);

            ResponseEntity<String> response = restTemplate.exchange(
            "http://localhost:9000/api/external-services",
                HttpMethod.GET,
                authRequest,
                String.class
            );

            return response.getBody(); // Deve restituire "Accesso autorizzato ✅"
        }

}
