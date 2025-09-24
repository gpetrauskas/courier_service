package com.example.courier.config;

import com.example.courier.service.auth.JwtService;
import com.example.courier.service.person.query.PersonLookupService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtService jwtService;
    private final PersonLookupService personLookupService;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;


    public SecurityConfig(JwtService jwtService, PersonLookupService personLookupService,
                          CustomAccessDeniedHandler customAccessDeniedHandler, JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint) {
        this.jwtService = jwtService;
        this.personLookupService = personLookupService;
        this.customAccessDeniedHandler = customAccessDeniedHandler;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity, CorsConfigurationSource corsConfigurationSource) throws Exception {
        httpSecurity
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth ->
                        auth
                                .requestMatchers(publicUrls()).permitAll()
                                .requestMatchers(adminUrls()).hasRole("ADMIN")
                                .requestMatchers(courierUrls()).hasAnyRole("COURIER", "ADMIN")
                                .anyRequest().authenticated())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(new JwtAuthenticationFilter(jwtService, personLookupService),
                        UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .accessDeniedHandler(customAccessDeniedHandler)
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint))
                .logout((logout) -> logout
                        .logoutUrl("/api/auth/logout")
                        .deleteCookies("jwt", "authToken")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .logoutSuccessHandler((request, response, authentication) -> {
                            response.setStatus(HttpServletResponse.SC_OK);
                        })
                );
        return httpSecurity.build();
    }

    /* Helper methods
    */

    private String[] publicUrls() {
        return new String[] {
                "/api/registration/register",
                "/api/auth/login",
                "/api/parcel/**",
                "/api/notifications/**",
                "/api/orders/**",
                "/api/person/**",
                "/api/paymentMethods/**",
                "/api/auth/cc/*",
                "/api/addresses/**"
        };
    }

    private String[] adminUrls() {
        return new String[] { "/api/admin/**", "/api/registration/registerCourier" };
    }

    private String[] courierUrls() {
        return new String[] { "/api/courier/**", "/api/deliveryTaskManagement/*" };
    }
}
