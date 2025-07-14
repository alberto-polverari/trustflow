package it.trustflow.document.filter;

import io.jsonwebtoken.Claims;
import it.trustflow.document.security.dto.AuthenticatedUser;
import it.trustflow.document.util.JwtUtil;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter implements Filter {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public void doFilter(ServletRequest request, ServletResponse servletResponse, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpReq = (HttpServletRequest) request;
        LOGGER.info("Processing request for URI: {}", httpReq.getRequestURI());

        String authHeader = httpReq.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String jwt = authHeader.substring(7);
            if (jwtUtil.isTokenValid(jwt)) {
                Claims claims = jwtUtil.extractClaims(jwt);
                httpReq.setAttribute("username", claims.getSubject());
                httpReq.setAttribute("tenantId", claims.get("tenantId"));
                httpReq.setAttribute("role", claims.get("role"));
                LOGGER.info("JWT token is valid for user: {}", claims.getSubject());
                AuthenticatedUser userDetails = new AuthenticatedUser(
                    claims.getSubject(),
                    claims.get("tenantId", Long.class),
                    claims.get("role", String.class)
                );
                String role = "ROLE_" + userDetails.getRole().toUpperCase();
                LOGGER.info("Setting role: {}", role);
                List<GrantedAuthority> authorities = List.of(() -> role);
                userDetails.setRoles(authorities);
                UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    authorities
                );
                MDC.put("tenantId", String.valueOf(claims.get("tenantId")));
                MDC.put("username", claims.getSubject());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        chain.doFilter(request, servletResponse);
    }
}
