package com.anndaan.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter, AuthenticationProvider authenticationProvider) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.authenticationProvider = authenticationProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        // Food Items rules
                        .requestMatchers("/api/food-items/available").hasAnyRole("CUSTOMER", "RIDER", "RESTAURANT")
                        .requestMatchers("/api/food-items/**").hasRole("RESTAURANT")
                        // Orders rules
                        .requestMatchers("/api/orders").hasRole("CUSTOMER")
                        .requestMatchers("/api/orders/customer").hasRole("CUSTOMER")
                        .requestMatchers("/api/orders/restaurant").hasRole("RESTAURANT")
                        .requestMatchers("/api/orders/{id}/status").hasAnyRole("CUSTOMER", "RESTAURANT")
                        // Deliveries rules
                        .requestMatchers("/api/deliveries/available").hasRole("RIDER")
                        .requestMatchers("/api/deliveries/rider").hasRole("RIDER")
                        .requestMatchers("/api/deliveries/{orderId}/claim").hasRole("RIDER")
                        .requestMatchers("/api/deliveries/{id}").hasAnyRole("CUSTOMER", "RIDER", "RESTAURANT")
                        .requestMatchers("/api/deliveries/{id}/**").hasRole("RIDER")
                        // General fallback
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
