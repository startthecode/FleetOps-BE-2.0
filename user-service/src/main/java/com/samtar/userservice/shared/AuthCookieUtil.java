package com.samtar.userservice.shared;


import jakarta.servlet.http.Cookie;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public final class AuthCookieUtil {

    private final Map<String, Object> authCookie;

    public AuthCookieUtil(@Value("${app.security.cookie.auth-token.name}") String cookieName,
                          @Value("${app.security.cookie.auth-token.http-only}") boolean httpOnly,
                          @Value("${app.security.cookie.auth-token.secure}") boolean secure,
                          @Value("${app.security.cookie.auth-token.same-site}") String sameSite,
                          @Value("${app.security.cookie.auth-token.path}") String path,
                          @Value("${app.security.cookie.auth-token.domain}") String domain,
                          @Value("${app.security.cookie.auth-token.expiry}") long expiry
    ) {
        this.authCookie = new HashMap<>();
        authCookie.put("name", cookieName);
        authCookie.put("httpOnly", httpOnly);
        authCookie.put("secure", secure);
        authCookie.put("sameSite", sameSite);
        authCookie.put("path", path);
        authCookie.put("domain", domain);
        authCookie.put("expiry", expiry);
    }


    public Cookie addAuthTokenCookie(String refreshToken) {
        Cookie cookie = new Cookie(
                (String) authCookie.get("name"),
                refreshToken
        );
        cookie.setHttpOnly((Boolean) authCookie.get("httpOnly"));
        cookie.setSecure((Boolean) authCookie.get("secure"));
        cookie.setPath((String) authCookie.get("path"));
        cookie.setDomain((String) authCookie.get("domain"));
        cookie.setMaxAge(((Long) authCookie.get("expiry")).intValue()); // if expiry is in seconds
        return cookie;
    }

}
