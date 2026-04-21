package com.ocp.backend.config;

import com.ocp.backend.filter.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    @Value("${cors.allowed-origin}")
    private String allowedOrigin;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // CORS - 允許瀏覽器非同源呼叫:前端 (localhost:5173) 呼叫後端 API
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(allowedOrigin));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        // allow cookies and Authorization header(JWT)
        config.setAllowCredentials(true);

        // UrlBasedCorsConfigurationSource是上面CORS設定的容器，registerCorsConfiguration是將設定套用到所有URL
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // 1. Auth & System
                        .requestMatchers("/api/auth/**", "/error").permitAll()

                        // 2. Swagger/OpenAPI Documentation
                        .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll()

                        // 3. Payment Callback (Must be public for ECPay)
                        .requestMatchers("/api/payment/callback").permitAll()

                        // 4. Public Course Data (GET only)
                        // Allow List of Courses
                        .requestMatchers(HttpMethod.GET, "/api/courses").permitAll()
                        // Allow Specific Course Detail (e.g. /api/courses/10)
                        .requestMatchers(HttpMethod.GET, "/api/courses/*").permitAll()
                        // Allow Syllabus (e.g. /api/courses/10/syllabus)
                        .requestMatchers(HttpMethod.GET, "/api/courses/*/syllabus").permitAll()
                        // Allow Reviews (e.g. /api/courses/10/reviews)
                        .requestMatchers(HttpMethod.GET, "/api/courses/*/reviews").permitAll()
                        // Allow Categories (for course filter/creation)
                        .requestMatchers(HttpMethod.GET, "/api/categories").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/categories/*").permitAll()

                        // 5. Static Files (Course Images)
                        .requestMatchers(HttpMethod.GET, "/uploads/**").permitAll()

                        // 6. Everything else requires login (check if SecurityContext has auth)
                        .anyRequest().authenticated())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}