package com.ditod.notes.web.auth;

import com.ditod.notes.domain.user.UserService;
import com.ditod.notes.web.auth.dto.LoginRequest;
import com.ditod.notes.web.auth.dto.SignupRequest;
import com.ditod.notes.web.user.dto.AuthUserDto;
import com.ditod.notes.web.user.dto.AuthUserResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@Tag(name = "authentication", description = "Manages login, registration and " + "logout")
public class AuthController {
    private final AuthService authService;
    private final UserService userService;

    public AuthController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    @PostMapping("/login")
    AuthUserResponse login(@RequestBody LoginRequest userRequest, HttpServletRequest request,
                           HttpServletResponse response) {
        authService.authenticate(userRequest, request, response);
        return new AuthUserResponse(
                userService.findByUsername(userRequest.username(), AuthUserDto.class));
    }

    @PostMapping("/logout")
    void logout(HttpServletRequest request) {
        authService.logout(request);
    }

    @PostMapping("/signup")
    AuthUserResponse signup(@RequestBody SignupRequest signupRequest, HttpServletRequest request,
                            HttpServletResponse response) {
        authService.onboard(signupRequest);
        authService.authenticate(
                new LoginRequest(signupRequest.username(), signupRequest.password(), false),
                request, response);
        return new AuthUserResponse(
                userService.findByUsername(signupRequest.username(), AuthUserDto.class));
    }
}
