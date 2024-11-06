package com.ditod.notes.utils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Optional;

@Component
public class VerifyCookieHelper {

    public void addVerifyCookie(HttpServletResponse response, VerifyCookieName cookieName, String value) {
        Cookie cookie = new Cookie(cookieName.name(), value);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(600);
        response.addCookie(cookie);
    }

    public void clearVerifyCookies(HttpServletRequest request, HttpServletResponse response) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (isVerifyCookie(cookie)) {
                    cookie.setValue(null);
                    cookie.setPath("/");
                    cookie.setMaxAge(0);
                    response.addCookie(cookie);
                }
            }
        }
    }

    private boolean isVerifyCookie(Cookie cookie) {
        try {
            VerifyCookieName.valueOf(cookie.getName());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public Optional<String> getVerifyCookieValue(HttpServletRequest request, VerifyCookieName verifyCookieName) {
        return Arrays.stream(request.getCookies()).filter(cookie -> cookie.getName().equals(verifyCookieName.name()))
                     .map(Cookie::getValue).findFirst();
    }
}
