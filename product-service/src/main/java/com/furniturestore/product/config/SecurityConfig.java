package com.furniturestore.product.config;

import com.furniturestore.product.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.*;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/actuator/health").permitAll()
                        // Admins can do everything; cashiers read-only
                        .requestMatchers(HttpMethod.GET, "/api/v1/products/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_CASHIER")
                        .requestMatchers(HttpMethod.GET, "/api/v1/categories/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_CASHIER")
                        .requestMatchers(HttpMethod.GET, "/api/v1/stock/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_CASHIER")
                        .requestMatchers("/api/v1/products/**", "/api/v1/categories/**", "/api/v1/stock/**").hasAuthority("ROLE_ADMIN")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
