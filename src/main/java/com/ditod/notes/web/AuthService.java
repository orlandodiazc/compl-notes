package com.ditod.notes.web;

import com.ditod.notes.auth.TokenService;
import com.ditod.notes.domain.user.User;
import com.ditod.notes.domain.user.UserService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    private final PasswordEncoder passwordEncoder;

    private final UserService userService;

    public AuthService(AuthenticationManager authenticationManager,
            TokenService tokenService, PasswordEncoder passwordEncoder,
            UserService userService) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
    }

    public String authenticate(LoginRequest userRequest) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userRequest.username(), userRequest.password()));
        return tokenService.generateToken(authentication);
    }

    public User register(LoginRequest user) {
        return userService.save(new User("example@mail.com", user.username(), passwordEncoder.encode(user.password()), "example name"));
    }
}
