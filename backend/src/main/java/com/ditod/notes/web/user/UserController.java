package com.ditod.notes.web.user;

import com.ditod.notes.auth.TotpService;
import com.ditod.notes.domain.exception.EntityAlreadyExistsException;
import com.ditod.notes.domain.user.User;
import com.ditod.notes.domain.user.UserRepository;
import com.ditod.notes.domain.user.UserService;
import com.ditod.notes.domain.verification.Verification;
import com.ditod.notes.domain.verification.VerificationRepository;
import com.ditod.notes.domain.verification.VerifyType;
import com.ditod.notes.utils.VerifyCookieHelper;
import com.ditod.notes.utils.VerifyCookieName;
import com.ditod.notes.web.auth.AuthService;
import com.ditod.notes.web.user.dto.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
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
    private final VerifyCookieHelper verifyCookieHelper;
    private final VerificationRepository verificationRepository;
    private final TotpService totpService;

    public UserController(UserService userService, AuthService authService, UserRepository userRepository,
                          VerifyCookieHelper verifyCookieHelper, VerificationRepository verificationRepository,
                          TotpService totpService) {
        this.userService = userService;
        this.authService = authService;
        this.userRepository = userRepository;
        this.verifyCookieHelper = verifyCookieHelper;
        this.verificationRepository = verificationRepository;
        this.totpService = totpService;
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
        if (auth == null || !auth.isAuthenticated()) return ResponseEntity.ok(new AuthUserResponse(null));
        Optional<AuthUserDto> user = userRepository.findByUsername(auth.getName(), AuthUserDto.class);
        return user.map(authUserDto -> ResponseEntity.ok(new AuthUserResponse(authUserDto))).orElseGet(() -> {
            authService.logout(request);
            return ResponseEntity.ok(new AuthUserResponse(null));
        });
    }

    @PatchMapping("/me")
    ResponseEntity<Void> updateMe(@RequestBody UpdateNamesRequest updateNamesRequest, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        String newUsername = updateNamesRequest.username();
        if (!newUsername.equals(user.getUsername()) && userService.existsByUsername(newUsername)) {
            throw new EntityAlreadyExistsException("user", "username");
        }

        user.setUsername(updateNamesRequest.username());
        user.setName(updateNamesRequest.name());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/me/change-password")
    ResponseEntity<ProblemDetail> changePassword(@RequestBody ChangePasswordRequest changePasswordRequest,
                                                 Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        if (authService.verifyPassword(user, changePasswordRequest.currentPassword())) {
            authService.updatePassword(user, changePasswordRequest.newPassword());
        } else {
            return ResponseEntity.badRequest()
                                 .body(ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Incorrect password"));
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/me/change-email")
    ResponseEntity<Void> changeEmail(@RequestBody ChangeEmailRequest changeEmailRequest, HttpServletResponse response) {
        String newEmail = changeEmailRequest.email();
        if (userRepository.existsByEmail(newEmail)) {
            throw new EntityAlreadyExistsException("user", "email");
        }
        var codeGenerationResult = totpService.generate();
        System.out.println(codeGenerationResult.code());
        verificationRepository.save(new Verification(codeGenerationResult.secret(), VerifyType.CHANGE_EMAIL, newEmail));
        verifyCookieHelper.addVerifyCookie(response, VerifyCookieName.NEW_EMAIL_ADDRESS, newEmail);
        return ResponseEntity.ok().build();
    }
}
