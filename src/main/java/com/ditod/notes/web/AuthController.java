package com.ditod.notes.web;

import com.ditod.notes.domain.exception.EntityAlreadyExistsException;
import com.ditod.notes.domain.user.UserRepository;
import com.ditod.notes.domain.user.UserService;
import com.ditod.notes.domain.user.dto.UserBaseResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;
    private final UserService userService;
    private final UserRepository userRepository;

    public AuthController(AuthService authService, UserService userService,
            UserRepository userRepository) {
        this.authService = authService;
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @GetMapping("/user")
    ResponseEntity<AuthUserResponse> authUser(Authentication auth,
            HttpServletResponse response) {
        if (!auth.isAuthenticated())
            return ResponseEntity.ok(new AuthUserResponse(null));
        Optional<UserBaseResponse> user = userRepository.findByUsername(auth.getName(), UserBaseResponse.class);
        if (user.isEmpty()) {
            authService.updateJwtCookieInResponse(response, null, 0);
            return ResponseEntity.ok(new AuthUserResponse(null));
        }
        return ResponseEntity.ok(new AuthUserResponse(user.get()));
    }

    @PostMapping("/login")
    private ResponseEntity<AuthUserResponse> login(
            @RequestBody LoginRequest userRequest,
            HttpServletResponse response) {
        String jwtToken = authService.authenticate(userRequest);
        authService.updateJwtCookieInResponse(response, jwtToken, userRequest.remember() ? 86400 : -1);
        return ResponseEntity.ok(new AuthUserResponse(userService.findByUsername(userRequest.username(), UserBaseResponse.class)));
    }

    @PostMapping("/logout")
    private ResponseEntity<Void> logout(HttpServletResponse response) {
        authService.updateJwtCookieInResponse(response, null, 0);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/signup")
    private ResponseEntity<AuthUserResponse> signup(
            @RequestBody SignupRequest signupRequest,
            HttpServletResponse response) {
        if (userRepository.existsByUsernameIgnoreCase(signupRequest.username())) {
            throw new EntityAlreadyExistsException("user", "username");
        }

        authService.signup(signupRequest);
        String jwtToken = authService.authenticate(new LoginRequest(signupRequest.username(), signupRequest.password(), true));
        authService.updateJwtCookieInResponse(response, jwtToken, 86400);
        return ResponseEntity.ok(new AuthUserResponse(userService.findByUsername(signupRequest.username(), UserBaseResponse.class)));
    }
}
