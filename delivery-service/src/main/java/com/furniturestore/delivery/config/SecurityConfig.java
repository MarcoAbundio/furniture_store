package com.furniturestore.delivery.config;

import com.furniturestore.delivery.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.*;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration @EnableWebSecurity @EnableMethodSecurity
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtFilter;
    public SecurityConfig(JwtAuthenticationFilter f) { this.jwtFilter = f; }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http.csrf(c -> c.disable())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/actuator/health").permitAll()
                        // Both roles can create/view deliveries and manage customers
                        .requestMatchers(HttpMethod.GET, "/api/v1/deliveries/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_CASHIER")
                        .requestMatchers(HttpMethod.POST, "/api/v1/deliveries/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_CASHIER")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/deliveries/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_CASHIER")
                        .requestMatchers(HttpMethod.GET, "/api/v1/customers/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_CASHIER")
                        .requestMatchers(HttpMethod.POST, "/api/v1/customers/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_CASHIER")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/customers/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_CASHIER")
                        // Only admin can cancel/delete deliveries or delete customers
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/deliveries/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/customers/**").hasAuthority("ROLE_ADMIN")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
