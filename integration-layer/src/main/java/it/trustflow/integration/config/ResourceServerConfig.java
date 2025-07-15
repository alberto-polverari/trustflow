package it.trustflow.integration.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.RequestMatcher;

@Configuration
public class ResourceServerConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Configura Authorization Server integrato
        OAuth2AuthorizationServerConfigurer authorizationConfigurer =
            new OAuth2AuthorizationServerConfigurer();

        RequestMatcher authEndpoints = authorizationConfigurer.getEndpointsMatcher();

        http
        .authorizeHttpRequests(auth -> auth
            .requestMatchers(authEndpoints).permitAll()      // ✅ endpoint /oauth2/* liberi
            .anyRequest().authenticated()                    // 🔐 tutto il resto protetto
        )
        .csrf(csrf -> csrf.ignoringRequestMatchers(authEndpoints))
        .oauth2ResourceServer(oauth2 -> oauth2.jwt())        // ✅ JWT obbligatorio
        .with(authorizationConfigurer, config -> {});

        return http.build();

    }

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails user = User.withUsername("admin")
        .password("{noop}admin")
        .roles("USER")
        .build();
        return new InMemoryUserDetailsManager(user);
    }
}
