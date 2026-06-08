package com.furniturestore.employee.config;

import com.furniturestore.employee.security.JwtAuthenticationFilter;
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
    public SecurityConfig(JwtAuthenticationFilter jwtFilter) { this.jwtFilter = jwtFilter; }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http.csrf(c -> c.disable())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/actuator/health").permitAll()
                        // Cashiers can only read employee info (non-salary data controlled by service layer)
                        .requestMatchers(HttpMethod.GET, "/api/v1/employees/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_CASHIER")
                        .requestMatchers(HttpMethod.GET, "/api/v1/departments/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_CASHIER")
                        .requestMatchers(HttpMethod.GET, "/api/v1/positions/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_CASHIER")
                        .requestMatchers("/api/v1/employees/**", "/api/v1/departments/**", "/api/v1/positions/**").hasAuthority("ROLE_ADMIN")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
