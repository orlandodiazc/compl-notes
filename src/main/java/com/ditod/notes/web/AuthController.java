package com.ditod.notes.web;

import com.ditod.notes.domain.user.UserService;
import com.ditod.notes.domain.user.dto.UserBaseResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;
    private final UserService userService;

    public AuthController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    @GetMapping("/user")
    ResponseEntity<?> authUser(JwtAuthenticationToken token) {
        if (token == null || !token.isAuthenticated())
            return ResponseEntity.ok(new AuthUserResponse(null));
        return ResponseEntity.ok(new AuthUserResponse(userService.findByUsername(token.getName(), UserBaseResponse.class)));
    }

    @PostMapping("/login")
    private ResponseEntity<AuthUserResponse> login(
            @ModelAttribute LoginRequest userRequest,
            HttpServletResponse response) {
        String jwtToken = authService.authenticate(userRequest);
        Cookie jwtTokenCookie = new Cookie("jwt", jwtToken);
        //TODO: handle remember cases
        jwtTokenCookie.setMaxAge(userRequest.remember() ? 86400 : 0);
        jwtTokenCookie.setSecure(true);
        jwtTokenCookie.setHttpOnly(true);
        jwtTokenCookie.setPath("/");
        response.addCookie(jwtTokenCookie);
        return ResponseEntity.ok(new AuthUserResponse(userService.findByUsername(userRequest.username(), UserBaseResponse.class)));
    }

    @PostMapping("/register")
    private ResponseEntity<?> register(@RequestBody LoginRequest user,
            HttpServletResponse response) {
        authService.register(user);
        return ResponseEntity.ok().build();
    }
}
