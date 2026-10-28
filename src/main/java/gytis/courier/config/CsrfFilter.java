package gytis.courier.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.*;

public class CsrfFilter extends OncePerRequestFilter {
    public final Set<String> UNSAFE = Set.of("POST", "PUT", "DELETE", "PATCH");
    public final Set<String> SKIP = Set.of("/api/auth", "/api/register", "/api/auth/refresh", "/api/auth/logout");

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (UNSAFE.contains(request.getMethod()) && !SKIP.contains(request.getRequestURI())) {
            String header = request.getHeader("X-XSRF-TOKEN");

            Cookie cookie = null;
            if (header != null && request.getCookies() != null) {
                for (Cookie c : request.getCookies()) {
                    if ("XSRF-TOKEN".equals(c.getName())) {
                        cookie = c;
                        break;
                    }
                }
            }

            boolean isFound = cookie != null && cookie.getValue().equals(header);

            if (!isFound) {
                System.out.println("token not found");
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.setContentType("application/json");
                response.getWriter().write("{\"message\": \"CSRF token not found\"}");
                return;
            }
        }

        System.out.println("all fine " + request.getMethod() + " " + request.getRequestURI());
        filterChain.doFilter(request, response);
    }
}
