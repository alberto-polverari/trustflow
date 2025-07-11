package it.trustflow.integration.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.RequestMatcher;

@Configuration
public class ResourceServerConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        OAuth2AuthorizationServerConfigurer authorizationServerConfigurer =
            new OAuth2AuthorizationServerConfigurer();

        RequestMatcher endpointsMatcher = authorizationServerConfigurer
            .getEndpointsMatcher();

        http
        .securityMatcher(endpointsMatcher) // matcha solo /oauth2/*
//        .securityMatcher("/api/**")
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/api/test/**").permitAll()
            .anyRequest().authenticated()
        )
        .csrf(csrf -> csrf.ignoringRequestMatchers(endpointsMatcher))
        .with(authorizationServerConfigurer, config -> {}); // ✅ nuovo approccio

        // chain secondaria per le API
//        http
//        .securityMatcher("/api/**")
//        .authorizeHttpRequests(auth -> auth
//            .requestMatchers("/api/test/**").permitAll()
//            .anyRequest().authenticated()
//        );
//        .oauth2ResourceServer(oauth2 -> oauth2
//            .jwt(Customizer.withDefaults())
//        );

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
