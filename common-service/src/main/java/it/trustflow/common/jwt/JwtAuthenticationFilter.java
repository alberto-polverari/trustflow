package it.trustflow.common.jwt;

import io.jsonwebtoken.Claims;
import it.trustflow.common.dto.AuthenticatedUser;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter implements Filter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
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
                UserDetails userDetails = new AuthenticatedUser(
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
//                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        chain.doFilter(request, response);
    }
}
