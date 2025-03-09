package com.example.courier.config;

import com.example.courier.domain.Person;
import com.example.courier.exception.UserNotFoundException;
import com.example.courier.service.AuthService;
import com.example.courier.service.JwtService;
import com.example.courier.util.CookieUtils;
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
import java.nio.file.AccessDeniedException;
import java.util.*;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    private final AuthService authService;

    private Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    public JwtAuthenticationFilter(JwtService jwtService, AuthService authService) {
        this.jwtService = jwtService;
        this.authService = authService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain)  throws ServletException, IOException {

        String token = extractTokenFromCookies(request, "jwt");
        String authToken = extractTokenFromCookies(request, "authToken");


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
                    throw new AccessDeniedException("Auth token mismatch");
                }

                Person person = authService.findByUsername(subject);

                Authentication authentication = new UsernamePasswordAuthenticationToken(person, null, person.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);

                logger.info("Authenticated user: {}", name);

            } catch (SignatureException e) {
                handleJwtException(request, response, "Invalid JWT signature: " + e.getMessage());
                return;
            } catch (ExpiredJwtException e) {
                handleJwtException(request, response, "JWT toke nis expired: " + e.getMessage());
                return;
            } catch (UserNotFoundException e) {
                handleJwtException(request, response, "User not found: " + e.getMessage());
                return;
            } catch (Exception e) {
                handleJwtException(request, response, "Could not set user authentication in security context: " + e.getMessage());
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    private void logoutOnWrongCookies(HttpServletRequest request, HttpServletResponse response) throws IOException {
        CookieUtils.clearAllCookies(response);
        SecurityContextHolder.clearContext();
        request.getSession().invalidate();
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT or AUTH token mismatch. Please login in again");
    }

    private String extractTokenFromCookies(HttpServletRequest request, String tokenName) {
        if (request.getCookies() == null) {
            return null;
        }

        return Arrays.stream(request.getCookies())
                .filter(c ->tokenName.equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }

    private void handleJwtException(HttpServletRequest request, HttpServletResponse response, String errorMessage) throws IOException {
        logger.warn(errorMessage);
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, errorMessage);
    }
}
