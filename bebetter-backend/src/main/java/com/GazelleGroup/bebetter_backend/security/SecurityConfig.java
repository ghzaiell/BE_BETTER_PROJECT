package com.GazelleGroup.bebetter_backend.security;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@AllArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http , AuthenticationManager authManager) throws Exception {
        http

                .csrf(csrf -> csrf.disable()) // disable CSRF for API
                .authenticationManager(authManager)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/v3/api-docs.yaml"
                        ).permitAll()
                        .requestMatchers("/auth/login", "/auth/register").permitAll()
                        .requestMatchers("/subject/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/user/{userId}").hasAnyRole("USER", "ADMIN") // Un user peut voir son propre profil
                        .requestMatchers(HttpMethod.GET, "/user/all").hasRole("ADMIN")      // Seul admin peut voir tous les users
                        .requestMatchers(HttpMethod.DELETE, "/user/delete/**").hasRole("ADMIN")  // Seul admin peut supprimer
                        .requestMatchers(HttpMethod.PUT, "/user/update/**").hasRole("ADMIN")     // Seul admin peut modifier

                        // allow register endpoint
                        .anyRequest().authenticated()// all other requests need auth

                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);


        return http.build();
    }
}


