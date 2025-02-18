package com.example.courier.util;

import com.example.courier.domain.Courier;
import com.example.courier.domain.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuthUtils {
    private static final Logger logger = LoggerFactory.getLogger(AuthUtils.class);

    public static Long getAuthenticatedPersonId() {
        Authentication authentication = getAuthentication();
        logger.info("who {}", authentication);
        logger.info("check {}", authentication.getPrincipal().getClass().getName());
        logger.info("checkk {}", authentication.getClass());

        if (authentication.isAuthenticated()) {
            Person person = (Person) authentication.getPrincipal();
            logger.info("class {}", person.getClass());
            return person.getId();
        }
        throw new IllegalStateException("User not authenticated");
    }

    private static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }
}
