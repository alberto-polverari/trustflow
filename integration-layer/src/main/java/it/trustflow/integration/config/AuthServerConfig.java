package it.trustflow.integration.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import it.trustflow.integration.security.Jwks;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;

import java.security.NoSuchAlgorithmException;
import java.util.UUID;

@Configuration
public class AuthServerConfig {

        @Bean
        public RegisteredClientRepository registeredClientRepository() {
            RegisteredClient client = RegisteredClient.withId(UUID.randomUUID().toString())
            .clientId("demo-client")
            .clientSecret("{noop}secret") // no password encoder
            .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
            .scope("read")
            .build();

            return new InMemoryRegisteredClientRepository(client);
        }

        @Bean
        public JWKSource<SecurityContext> jwkSource() throws NoSuchAlgorithmException {
            RSAKey rsa = Jwks.generateRsa();
            JWKSet jwkSet = new JWKSet(rsa);
            return (jwkSelector, context) -> jwkSelector.select(jwkSet);
        }

        @Bean
        public AuthorizationServerSettings authorizationServerSettings() {
            return AuthorizationServerSettings.builder()
            .issuer("http://localhost:9000")
            .build();
        }

}
