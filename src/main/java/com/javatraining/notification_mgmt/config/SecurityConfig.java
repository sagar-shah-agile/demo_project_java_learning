package com.javatraining.notification_mgmt.config;

import com.javatraining.notification_mgmt.security.CustomUserDetailsService;
import com.javatraining.notification_mgmt.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)// ✅ enables @PreAuthorize, @PostAuthorize
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomUserDetailsService customUserDetailsService;

    /**
     * ✅ Define the security filter chain.
     * - Disable CSRF (for APIs)
     * - Allow some endpoints without authentication
     * - Require JWT auth for others
     * - Add JwtAuthenticationFilter before UsernamePasswordAuthenticationFilter
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception
    {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth

                        // 🔹 Public endpoints
                        // login & register
                        .requestMatchers("/api/auth/**").permitAll()
                        // get user by id, get all users, create user, update user by id, delete user by id
                        .requestMatchers("/api/users/**").permitAll()
                        // allow swagger UI + API docs
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/v3/api-docs.yaml",
                                "/webjars/**").permitAll()

                        // 🔹 Secured endpoints
                        .requestMatchers(HttpMethod.PUT, "/api/users/me").authenticated()    // update logged-in user
                        .requestMatchers(HttpMethod.DELETE, "/api/users/me").authenticated() // delete logged-in user

                        // Any other request must be authenticated
                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults()); // Remove form login

        return http.build();
    }

    /**
     * ✅ Configure the AuthenticationProvider (how Spring loads users + checks passwords).
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(customUserDetailsService); // loads user from DB
        provider.setPasswordEncoder(passwordEncoder()); // checks password using BCrypt
        return provider;
    }

    /**
     * ✅ AuthenticationManager (needed for login endpoint).
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * ✅ Password encoder (BCrypt).
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}