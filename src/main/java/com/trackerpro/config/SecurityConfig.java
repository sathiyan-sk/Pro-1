package com.trackerpro.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Disable CSRF for API endpoints
            .csrf(csrf -> csrf.disable())
            
            // Configure CORS
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            
            // Configure session management
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            // Configure authorization
            .authorizeHttpRequests(authz -> authz
                // Allow public access to static resources
                .requestMatchers(
                    "/",
                    "/index.html",
                    "/loginPage.html", 
                    "/registerPage.html",
                    "/adminPage.html",
                    "/forget.html",
                    "/login",
                    "/register", 
                    "/admin",
                    "/forgot",
                    "/*.png",
                    "/*.jpg", 
                    "/*.jpeg",
                    "/*.gif",
                    "/*.svg",
                    "/*.css",
                    "/*.js",
                    "/*.ico",
                    "/static/**",
                    "/public/**"
                ).permitAll()
                
                // Allow public access to authentication endpoints
                .requestMatchers(
                    "/api/login",
                    "/api/auth/register",
                    "/api/logout",
                    "/api/auth/status"
                ).permitAll()
                
                // Temporarily allow admin endpoints for development
                .requestMatchers("/api/admin/**").permitAll()
                
                // Allow H2 console in development
                .requestMatchers("/h2-console/**").permitAll()
                
                // All other API requests need authentication
                .requestMatchers("/api/**").authenticated()
                
                // Allow all other requests
                .anyRequest().permitAll()
            )
            
            // Disable frame options for H2 console
            .headers(headers -> headers.frameOptions().disable());
        
        return http.build();
    }
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Allow all origins in development
        configuration.setAllowedOriginPatterns(List.of("*"));
        
        // Allow specific methods
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        
        // Allow specific headers
        configuration.setAllowedHeaders(Arrays.asList(
            "Authorization", 
            "Content-Type", 
            "X-Requested-With",
            "Accept",
            "Origin",
            "Access-Control-Request-Method",
            "Access-Control-Request-Headers"
        ));
        
        // Allow credentials
        configuration.setAllowCredentials(true);
        
        // Cache preflight response
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
}