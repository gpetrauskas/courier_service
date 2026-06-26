package gytis.courier.config;

import gytis.courier.adapter.out.jwt.JwtService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
    private final JwtService jwtService;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;


    public SecurityConfig(JwtService jwtService,
                          CustomAccessDeniedHandler customAccessDeniedHandler, JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint) {
        this.jwtService = jwtService;
        this.customAccessDeniedHandler = customAccessDeniedHandler;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity, CorsConfigurationSource corsConfigurationSource) throws Exception {
        httpSecurity
                .cors(cors ->
                        cors.configurationSource(corsConfigurationSource))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth ->
                        auth
                                .requestMatchers(publicUrls()).permitAll()
                                .requestMatchers(adminUrls()).hasRole("ADMIN")
                                .requestMatchers(courierUrls()).hasAnyRole("COURIER", "ADMIN")
                                .anyRequest().authenticated())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(new JwtAuthenticationFilter(jwtService),
                        UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exceptionHandling ->
                        exceptionHandling
                                .accessDeniedHandler(customAccessDeniedHandler)
                                .authenticationEntryPoint(jwtAuthenticationEntryPoint))
                .logout((logout) ->
                        logout
                                .logoutUrl("/api/auth/logout")
                                .deleteCookies("jwt")
                                .deleteCookies("refresh")
                                .clearAuthentication(true)
                                .logoutSuccessHandler((request, response, authentication) ->
                                        response.setStatus(HttpServletResponse.SC_OK))
                );
        return httpSecurity.build();
    }

    /* Helper methods
    */

    private String[] publicUrls() {
        return new String[] {
                "/api/register",
                "/api/auth/**",
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
        return new String[] {
                "/api/admin/**",
                "/api/registration/registerCourier"
        };
    }

    private String[] courierUrls() {
        return new String[] {
                "/api/courier/**",
                "/api/deliveryTaskManagement/*"
        };
    }
}
