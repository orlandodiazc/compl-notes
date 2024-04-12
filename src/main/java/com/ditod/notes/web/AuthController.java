package com.ditod.notes.web;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping
    @RequestMapping("/login")
    private ResponseEntity<?> login(@RequestBody LoginRequest user,
            HttpServletResponse response) {
        String jwtToken = authService.authenticate(user);
        Cookie jwtTokenCookie = new Cookie("jwt", jwtToken);
        jwtTokenCookie.setMaxAge(86400);
        jwtTokenCookie.setSecure(true);
        jwtTokenCookie.setHttpOnly(true);
        jwtTokenCookie.setPath("/");
        response.addCookie(jwtTokenCookie);
        return ResponseEntity.ok().build();
    }

    @PostMapping
    @RequestMapping("/register")
    private ResponseEntity<?> register(@RequestBody LoginRequest user,
            HttpServletResponse response) {
        authService.register(user);
        return ResponseEntity.ok().build();
    }
}
