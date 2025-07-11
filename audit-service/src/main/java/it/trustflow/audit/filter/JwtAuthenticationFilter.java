package it.trustflow.audit.filter;

import io.jsonwebtoken.Claims;
import it.trustflow.audit.security.dto.AuthenticatedUser;
import it.trustflow.audit.util.JwtUtil;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter implements Filter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public void doFilter(ServletRequest request, ServletResponse servletResponse, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpReq = (HttpServletRequest) request;
        String authHeader = httpReq.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String jwt = authHeader.substring(7);
            if (jwtUtil.isTokenValid(jwt)) {
                Claims claims = jwtUtil.extractClaims(jwt);
                httpReq.setAttribute("username", claims.getSubject());
                httpReq.setAttribute("tenantId", claims.get("tenantId"));
                httpReq.setAttribute("role", claims.get("role"));
                AuthenticatedUser userDetails = new AuthenticatedUser(
                    claims.getSubject(),
                    claims.get("tenantId", Long.class),
                    claims.get("role", String.class)
                );
                UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
                );
                MDC.put("tenantId", String.valueOf(claims.get("tenantId")));
                MDC.put("username", claims.getSubject());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        chain.doFilter(request, servletResponse);
    }
}
