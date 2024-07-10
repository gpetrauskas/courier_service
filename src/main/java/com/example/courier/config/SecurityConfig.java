package com.example.courier.config;

import com.example.courier.service.JwtService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.authentication.configuration.EnableGlobalAuthentication;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

@Configuration
@EnableMethodSecurity
public class SecurityConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    @Autowired
    private JwtService jwtService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity, CorsConfigurationSource corsConfigurationSource) throws Exception {
        httpSecurity
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth ->
                        auth
                                .requestMatchers("/css/**", "/js/**", "/images/**", "/", "/courier/components/**", "/courier/**").permitAll()
                                .requestMatchers("/*", "/testpage/**").permitAll()
                                .requestMatchers("/api/user/register", "/api/user/login",
                                        "/api/orders/trackOrder/{id}", "/api/orders/**",
                                        "/api/paymentMethods/**", "/api/user/cc/*", "/api/addresses/**").permitAll()
                                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                                .requestMatchers("/api/courier/**").hasAnyRole("COURIER", "ADMIN")
                                .anyRequest().authenticated())
                .addFilterBefore(new JwtAuthenticationFilter(jwtService),
                        UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(e -> e.authenticationEntryPoint((request, response, authException) -> {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage());
                }));
        return httpSecurity.build();
    }

    /*
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Collections.singletonList("http://localhost:4200"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

     */
}
