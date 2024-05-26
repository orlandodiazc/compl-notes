package com.ditod.notes.web.auth;

import com.ditod.notes.domain.exception.EntityAlreadyExistsException;
import com.ditod.notes.domain.user.UserRepository;
import com.ditod.notes.domain.user.UserService;
import com.ditod.notes.domain.user.dto.AuthUserDto;
import com.ditod.notes.web.auth.dto.AuthUserResponse;
import com.ditod.notes.web.auth.dto.LoginRequest;
import com.ditod.notes.web.auth.dto.SignupRequest;
import jakarta.servlet.http.HttpServletRequest;
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
    ResponseEntity<AuthUserResponse> getAuthUser(Authentication auth,
            HttpServletResponse response) {
        if (auth == null || !auth.isAuthenticated())
            return ResponseEntity.ok(new AuthUserResponse(null));
        Optional<AuthUserDto> user = userRepository.findByUsername(auth.getName(), AuthUserDto.class);
        return user.map(authUserDto -> ResponseEntity.ok(new AuthUserResponse(authUserDto)))
                .orElseGet(() -> ResponseEntity.ok(new AuthUserResponse(null)));
    }

    @PostMapping("/login")
    private ResponseEntity<AuthUserResponse> login(
            @RequestBody LoginRequest userRequest, HttpServletRequest request,
            HttpServletResponse response) {
        authService.authenticate(userRequest, request, response);
        return ResponseEntity.ok(new AuthUserResponse(userService.findByUsername(userRequest.username(), AuthUserDto.class)));
    }

    @PostMapping("/logout")
    private ResponseEntity<Void> logout(HttpServletRequest request) {
        authService.logout(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/signup")
    private ResponseEntity<AuthUserResponse> signup(
            @RequestBody SignupRequest signupRequest,
            HttpServletRequest request, HttpServletResponse response) {
        if (userRepository.existsByUsernameIgnoreCase(signupRequest.username())) {
            throw new EntityAlreadyExistsException("user", "username");
        }
        authService.signup(signupRequest);
        authService.authenticate(new LoginRequest(signupRequest.username(), signupRequest.password(), false), request, response);
        return ResponseEntity.ok(new AuthUserResponse(userService.findByUsername(signupRequest.username(), AuthUserDto.class)));
    }
}
