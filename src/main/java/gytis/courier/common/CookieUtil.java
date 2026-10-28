package gytis.courier.common;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Arrays;
import java.util.UUID;

public class CookieUtil {
    public static final String JWT = "jwt";
    public static final String REFRESH = "refresh";
    public static final String CSRF = "XSRF-TOKEN";

    public static String getToken(HttpServletRequest request, String tokenName) {
        if (request.getCookies() == null) return null;

        return Arrays.stream(request.getCookies())
                .filter(t -> tokenName.equals(t.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }

    public static Cookie build(String name, String value, int maxAge, boolean httpOnly) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setAttribute("SameSite", "Strict");
        cookie.setMaxAge(maxAge);
        cookie.setHttpOnly(httpOnly);

        return cookie;
    }

    public static Cookie createAuthCookie(String name, String token, int maxAge) {
        return build(name, token, maxAge, true);
    }

    public static Cookie createCsrfCookie(int maxAge) {
        return build(CSRF, UUID.randomUUID().toString(), maxAge, false);
    }
}
