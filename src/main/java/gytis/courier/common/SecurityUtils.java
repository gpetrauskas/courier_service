package gytis.courier.common;

import gytis.courier.adapter.in.security.AuthenticatedPerson;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {
    public static String getCurrentPersonEmail() {
        UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        if (auth == null) {
            return "SYSTEM";
        }

        AuthenticatedPerson ap = (AuthenticatedPerson) auth.getPrincipal();

        return ap.email();
    }
}
