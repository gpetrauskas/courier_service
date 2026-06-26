package gytis.courier.config;

import gytis.courier.adapter.in.security.AuthenticatedPerson;
import gytis.courier.adapter.out.jwt.JwtService;
import gytis.courier.application.port.out.auth.JwtClaims;
import gytis.courier.common.CookieUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.*;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response,
                                    @Nonnull FilterChain filterChain)  throws ServletException, IOException {
        String token = CookieUtil.getToken(request, "jwt");

        if (request.getRequestURI().equals("/api/auth/refresh")) {
            filterChain.doFilter(request, response);
            return;
        }

        if (StringUtils.hasText(token)) {
            try {
                logger.info("validating JWT");
                authenticatePerson(jwtService.validateToken(token));
            } catch (SignatureException e) {
                handleJwtException(response, "Invalid JWT signature: " + e.getMessage());
                return;
            } catch (ExpiredJwtException e) {
                handleJwtException(response, "JWT toke nis expired: " + e.getMessage());
                return;
            } catch (Exception e) {
                handleJwtException(response, "Could not set user authentication in security context: " + e.getMessage());
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    private void authenticatePerson(JwtClaims claims) {
        AuthenticatedPerson ap = new AuthenticatedPerson(
                claims.id(),
                claims.subject(),
                claims.role(),
                claims.name()
        );
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                ap,
                null,
                Collections.singleton(new SimpleGrantedAuthority("ROLE_" + claims.role()))
        );

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        logger.info("Authenticated user: {}", claims.subject());
    }

    private void handleJwtException(HttpServletResponse response, String errorMessage) throws IOException {
        logger.warn(errorMessage);
        if (!response.isCommitted()) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("error: " + errorMessage);
            response.getWriter().flush();
        } else {
            logger.warn("Response already commited");
        }
    }
}
