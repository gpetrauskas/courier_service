package com.example.courier.config;

import com.example.courier.domain.Person;
import com.example.courier.dto.jwt.JwtClaims;
import com.example.courier.exception.UserNotFoundException;
import com.example.courier.service.auth.JwtService;
import com.example.courier.service.person.PersonService;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.*;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final PersonService personService;
    private Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    public JwtAuthenticationFilter(JwtService jwtService, PersonService personService) {
        this.jwtService = jwtService;
        this.personService = personService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain)  throws ServletException, IOException {
        String token = extractTokenFromCookies(request, "jwt");
        String authToken = extractTokenFromCookies(request, "authToken");

        if (StringUtils.hasText(token) && StringUtils.hasText(authToken)) {
            try {
                logger.info("validating JWT and Auth token");

                if (!validateTokens(token, authToken)) {
                    logoutOnWrongCookies(response);
                    return;
                }

                JwtClaims claims = jwtService.validateToken(token);
                authenticatePerson(claims.subject());

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

    private void authenticatePerson(String subject) {
        Person person = personService.findByUsername(subject);
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                person, null, person.getAuthorities()
        );

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        logger.info("Authenticated user: {}", subject);
    }

    private boolean validateTokens(String token, String authToken) {
        try {
            JwtClaims claims = jwtService.validateToken(token);
            String expectedAuthToken = claims.authToken();
            String decryptedAuthToken = jwtService.decryptAuthToken(authToken);

            if (!decryptedAuthToken.equals(expectedAuthToken)) {
                logger.warn("Auth token mismatch detected. Logging user off");
                return false;
            }
            return true;
        } catch (Exception e) {
            logger.warn("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    private void logoutOnWrongCookies(HttpServletResponse response) throws IOException {
        if (response.isCommitted()) {
            logger.warn("Resposne already commited, skipping logout");
            return;
        }

        CookieUtils.clearAllCookies(response);
        SecurityContextHolder.clearContext();
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT or AUTH token mismatch. Please login in again");
    }

    private String extractTokenFromCookies(HttpServletRequest request, String tokenName) {
        if (request.getCookies() == null) return null;

        return Arrays.stream(request.getCookies())
                .filter(c ->tokenName.equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }

    private void handleJwtException(HttpServletRequest request, HttpServletResponse response, String errorMessage) throws IOException {
        logger.warn(errorMessage);
        if (!response.isCommitted()) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"" + errorMessage + "\"}");
            response.getWriter().flush();
        } else {
            logger.warn("Response already commited, skipping sendError()");
        }
    }
}
