package com.ditod.notes.web;

import com.ditod.notes.domain.exception.EntityAlreadyExistsException;
import com.ditod.notes.domain.user.UserService;
import com.ditod.notes.domain.user.dto.UserBaseResponse;
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
            @RequestBody LoginRequest userRequest,
            HttpServletResponse response) {
        String jwtToken = authService.authenticate(userRequest);
        authService.addJwtCookieToResponse(response, jwtToken, userRequest.remember() ? 86400 : 0);
        return ResponseEntity.ok(new AuthUserResponse(userService.findByUsername(userRequest.username(), UserBaseResponse.class)));
    }

    @PostMapping("/signup")
    private ResponseEntity<?> signup(@RequestBody SignupRequest signupRequest,
            HttpServletResponse response) {
        if (!userService.existsByUsername(signupRequest.username())) {
            throw new EntityAlreadyExistsException("user", "username");
        }

        authService.signup(signupRequest);
        String jwtToken = authService.authenticate(new LoginRequest(signupRequest.username(), signupRequest.password(), true));
        authService.addJwtCookieToResponse(response, jwtToken, 86400);
        return ResponseEntity.ok(new AuthUserResponse(userService.findByUsername(signupRequest.username(), UserBaseResponse.class)));
    }
}
