package com.ditod.notes.auth;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.oauth2.server.resource.web.DefaultBearerTokenResolver;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

@Component
public class CookieTokenResolver implements BearerTokenResolver {

    @Override
    public String resolve(HttpServletRequest request) {
        Cookie cookie = WebUtils.getCookie(request, "jwt");
        if (cookie != null) {
            return cookie.getValue();
        }
        return new DefaultBearerTokenResolver().resolve(request);
    }
}
