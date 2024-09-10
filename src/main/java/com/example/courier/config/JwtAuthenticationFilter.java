package com.example.courier.config;

import com.example.courier.service.JwtService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    private Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain)  throws ServletException, IOException {

        Cookie[] cookies = request.getCookies();
        String token = null;
        String authToken = null;

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("authToken")) {
                    authToken = cookie.getValue();
                    logger.info("auth token found {}", authToken);
                } else if (cookie.getName().equals("jwt")) {
                    token = cookie.getValue();
                    logger.info("jwt token found {}", token);
                }
            }
        }

        if (token != null && !token.isEmpty() && authToken != null && !authToken.isEmpty()) {
            try {
                logger.info("Ready to validate token");
                Map<String, String> authDetails = jwtService.validateToken(token);
                logger.info("Token passed validation");
                String subject = authDetails.get("subject");
                String role = authDetails.get("role");
                String name = authDetails.get("name");
                String authTokenFromJWT = authDetails.get("authToken");

                if (!jwtService.decryptAuthToken(authToken).equals(authTokenFromJWT)) {
                    logger.warn("Auth token mismatch detected. logging user out");
                    logger.info("auth token {} authTokenFromJWT {}", authToken, authTokenFromJWT);
                    logoutOnWrongCookies(request, response);
                    logger.info("User logged out successfully");
                    return;
                }

                List<GrantedAuthority> authorities = new ArrayList<>();
                authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
                Authentication auth = new UsernamePasswordAuthenticationToken(subject, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(auth);

                logger.info("Authenticated user: {}", name);

            } catch (SignatureException e) {
                logoutOnWrongCookies(request, response);
                logger.warn("Invalid JWT signature: {}", e.getMessage());
            } catch (ExpiredJwtException e) {
                logoutOnWrongCookies(request, response);
                logger.warn("JWT token is expired: {}", e.getMessage());
            } catch (Exception e) {
                logoutOnWrongCookies(request, response);
                logger.error("Could not set user authentication in security context.", e);
            }
        }
        filterChain.doFilter(request, response);
    }

    private void logoutOnWrongCookies(HttpServletRequest request, HttpServletResponse response) throws IOException {
        CookieUtils.clearAllCookies(response);
        SecurityContextHolder.clearContext();
        request.getSession().invalidate();
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Auth token mismatch. Please login in again");
    }

}
