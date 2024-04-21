package com.ditod.notes.web;

import com.ditod.notes.auth.TokenService;
import com.ditod.notes.domain.user.User;
import com.ditod.notes.domain.user.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
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

    public void signup(SignupRequest user) {
        userService.save(new User(user.email(), user.username(), passwordEncoder.encode(user.password()), user.name()));
    }

    public void addJwtCookieToResponse(HttpServletResponse response,
            String jwtToken, int maxAge) {
        Cookie jwtTokenCookie = new Cookie("jwt", jwtToken);
        jwtTokenCookie.setMaxAge(maxAge);
        jwtTokenCookie.setSecure(true);
        jwtTokenCookie.setHttpOnly(true);
        jwtTokenCookie.setPath("/");
        response.addCookie(jwtTokenCookie);
    }
}
