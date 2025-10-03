package com.javatraining.notification_mgmt.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component   // ✅ Makes Spring manage this filter as a bean
@RequiredArgsConstructor   // ✅ Injects final fields (jwtUtil, userDetailsService)
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;   // ✅ Utility class for token generation/validation
    private final CustomUserDetailsService userDetailsService;  // ✅ Loads user from DB

    /**
     * ✅ This method runs once per request.
     * It extracts JWT token, validates it, and sets the authenticated user into Spring Security context.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // 1️⃣ Extract the Authorization header (should contain Bearer token)
        final String authHeader = request.getHeader("Authorization");
        String username = null;
        String token = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            // ✅ Remove "Bearer " prefix to get actual JWT
            token = authHeader.substring(7);
            try {
                // ✅ Extract username (usually email) from JWT
                username = jwtService.extractUsername(token);
            } catch (Exception e) {
                log.warn("Unable to extract JWT token: {}", e.getMessage());
            }
        }

        // 2️⃣ Validate the token and set authentication
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // ✅ Load user details from DB
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // ✅ Validate token (check signature, expiry, etc.)
            if (jwtService.validateToken(token, userDetails)) {
                // ✅ Build an authentication object for Spring Security
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,   // principal
                                null,          // no credentials (already authenticated via JWT)
                                userDetails.getAuthorities() // roles/permissions
                        );

                // ✅ Attach request details (like IP, session) for auditing
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // ✅ Store authentication in SecurityContext → now user is authenticated
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // 3️⃣ Pass the request forward in the filter chain (important!)
        filterChain.doFilter(request, response);
    }
}
