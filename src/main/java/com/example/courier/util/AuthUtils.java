package com.example.courier.util;

import com.example.courier.domain.Person;
import com.example.courier.exception.UnauthorizedAccessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class AuthUtils {
    private static final Logger logger = LoggerFactory.getLogger(AuthUtils.class);

    private AuthUtils() {}

    /**
     * Return the ID of authenticated person
     *
     * @return the ID of the authenticated person
     */
    public static Long getAuthenticatedPersonId() {
        Authentication authentication = getAuthentication();
        logger.info("who {}", authentication);
        logger.info("check {}", authentication.getPrincipal().getClass().getName());
        logger.info("checkk {}", authentication.getClass());

        if (authentication.isAuthenticated()) {
            Person person = (Person) authentication.getPrincipal();
            logger.info("class {}", person.getClass());
            logger.info("id: {}", person.getId());
            return person.getId();
        }
        throw new IllegalStateException("User not authenticated");
    }

    /**
     * Checks if the authenticated Person is Admin
     *
     * @return true if person is admin, false otherwise
     * @throws UnauthorizedAccessException if the person is not authenticated
     */
    public static boolean isAdmin() {
        Authentication authentication = getAuthentication();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
        logger.info("User {} is admin: {}",authentication.getDetails(), isAdmin);
        logger.debug("User {} is admin: {}",authentication.getPrincipal(), isAdmin);
        return isAdmin;
    }

    /**
     * Returns the authenticated person as specified type
     *
     * @param tClass the class of the expected type (Admin.class, Courier.class, User.class)
     * @return the authenticated person as specified type
     * @param <T> type of the authenticated person, which must extend {@link Person}
     * @throws UnauthorizedAccessException if the user is not authenticated
     *  or the principal is not of expected tpe
     */
    public static <T extends Person> T getAuthenticated(Class<T> tClass) {
       Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
       if (authentication == null || !authentication.isAuthenticated()) {
           throw new UnauthorizedAccessException("Person not authorized");
       }

       Object principal = authentication.getPrincipal();

       if (principal instanceof Person) {
           if (tClass.isInstance(principal)) {
               logger.info("Authorized person is {}", tClass.getSimpleName());
               return tClass.cast(principal);
           }
           throw new UnauthorizedAccessException("Authenticated person is not of type " + tClass.getSimpleName());
       }
       throw new UnauthorizedAccessException("invalid authentication details");
    }

    /**
     * Returns current authentication object
     *
     * @return current authentication object
     * @throws UnauthorizedAccessException if the authentication object is null
     */
    private static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }
}
