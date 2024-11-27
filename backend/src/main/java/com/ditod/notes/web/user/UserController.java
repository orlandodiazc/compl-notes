package com.ditod.notes.web.user;

import com.ditod.notes.domain.user.UserRepository;
import com.ditod.notes.domain.user.UserService;
import com.ditod.notes.web.auth.AuthService;
import com.ditod.notes.web.user.dto.AuthUserDto;
import com.ditod.notes.web.user.dto.AuthUserResponse;
import com.ditod.notes.web.user.dto.UserFilteredResponse;
import com.ditod.notes.web.user.dto.UserSummaryResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
@Tag(name = "user", description = "Everything about users")
public class UserController {

    private final UserService userService;
    private final AuthService authService;
    private final UserRepository userRepository;

    public UserController(UserService userService, AuthService authService,
                          UserRepository userRepository) {
        this.userService = userService;
        this.authService = authService;
        this.userRepository = userRepository;
    }

    @GetMapping
    List<UserFilteredResponse> listUsers(
            @RequestParam(required = false, defaultValue = "") String filter) {
        return userService.findFilteredUsers(filter);
    }

    @GetMapping("/{username}")
    UserSummaryResponse getUser(@PathVariable String username) {
        return userService.findByUsername(username, UserSummaryResponse.class);
    }

    @GetMapping("/me")
    AuthUserResponse getMe(Authentication auth, HttpServletRequest request) {
        if (auth == null || !auth.isAuthenticated()) return new AuthUserResponse(null);
        Optional<AuthUserDto> user = userRepository.findByUsername(auth.getName(),
                                                                   AuthUserDto.class);
        return user.map(AuthUserResponse::new).orElseGet(() -> {
            authService.logout(request);
            return new AuthUserResponse(null);
        });
    }
}
