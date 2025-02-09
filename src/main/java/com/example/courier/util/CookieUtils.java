package com.example.courier.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CookieUtils {

    private static Logger logger = LoggerFactory.getLogger(CookieUtils.class);

    private static void clearCookie(HttpServletResponse response, String cookieName) {
        Cookie cookie = new Cookie(cookieName, "");
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        logger.info("Cookie {} was removed successfully", cookieName);
    }

    public static void clearAllCookies(HttpServletResponse response) {
        clearCookie(response, "jwt");
        clearCookie(response, "authToken");
    }
}
