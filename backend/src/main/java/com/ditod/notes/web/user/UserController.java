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
import org.springframework.http.ResponseEntity;
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
    ResponseEntity<List<UserFilteredResponse>> listUsers(
            @RequestParam(required = false, defaultValue = "") String filter) {
        return ResponseEntity.ok(userService.findFilteredUsers(filter));
    }

    @GetMapping("/{username}")
    ResponseEntity<UserSummaryResponse> getUser(@PathVariable String username) {
        return ResponseEntity.ok(userService.findByUsername(username, UserSummaryResponse.class));
    }

    @GetMapping("/me")
    ResponseEntity<AuthUserResponse> getMe(Authentication auth, HttpServletRequest request) {
        if (auth == null || !auth.isAuthenticated())
            return ResponseEntity.ok(new AuthUserResponse(null));
        Optional<AuthUserDto> user = userRepository.findByUsername(auth.getName(),
                                                                   AuthUserDto.class);
        return user.map(authUserDto -> ResponseEntity.ok(new AuthUserResponse(authUserDto)))
                   .orElseGet(() -> {
                       authService.logout(request);
                       return ResponseEntity.ok(new AuthUserResponse(null));
                   });
    }
}
