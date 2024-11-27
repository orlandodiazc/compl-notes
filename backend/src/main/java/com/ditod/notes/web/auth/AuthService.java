package com.ditod.notes.web.auth;

import com.ditod.notes.domain.role.RoleRepository;
import com.ditod.notes.domain.user.User;
import com.ditod.notes.domain.user.UserService;
import com.ditod.notes.web.auth.dto.LoginRequest;
import com.ditod.notes.web.auth.dto.SignupRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthService {
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final SecurityContextHolderStrategy securityContextHolderStrategy = SecurityContextHolder.getContextHolderStrategy();
    private final SecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();
    private final SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();

    public AuthService(PasswordEncoder passwordEncoder, RoleRepository roleRepository,
                       UserService userService, AuthenticationManager authenticationManager) {
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;

        this.userService = userService;
        this.authenticationManager = authenticationManager;
    }

    public void authenticate(LoginRequest loginRequest, HttpServletRequest request,
                             HttpServletResponse response) {
        Authentication authenticationRequest = UsernamePasswordAuthenticationToken.unauthenticated(
                loginRequest.username(), loginRequest.password());
        Authentication authentication = this.authenticationManager.authenticate(
                authenticationRequest);
        SecurityContext context = securityContextHolderStrategy.createEmptyContext();
        context.setAuthentication(authentication);
        securityContextHolderStrategy.setContext(context);
        securityContextRepository.saveContext(context, request, response);
    }

    public void onboard(SignupRequest user) {
        userService.save(
                new User(user.email(), user.username(), passwordEncoder.encode(user.password()),
                         user.name(), List.of(roleRepository.findByName("ROLE_USER")), null));

    }

    public void logout(HttpServletRequest request) {
        logoutHandler.logout(request, null, null);
    }
}
