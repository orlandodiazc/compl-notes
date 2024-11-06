package com.ditod.notes.web.auth;

import com.ditod.notes.auth.TotpService;
import com.ditod.notes.domain.exception.EntityAlreadyExistsException;
import com.ditod.notes.domain.exception.EntityNotFoundException;
import com.ditod.notes.domain.user.User;
import com.ditod.notes.domain.user.UserRepository;
import com.ditod.notes.domain.user.UserService;
import com.ditod.notes.domain.verification.Verification;
import com.ditod.notes.domain.verification.VerificationRepository;
import com.ditod.notes.domain.verification.VerifyType;
import com.ditod.notes.utils.VerifyCookieHelper;
import com.ditod.notes.utils.VerifyCookieName;
import com.ditod.notes.web.auth.dto.*;
import com.ditod.notes.web.user.dto.*;
import com.resend.Resend;
import com.resend.services.emails.model.CreateEmailOptions;
import dev.samstevens.totp.exceptions.CodeGenerationException;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
@Tag(name = "authentication", description = "Manages login, registration and " + "logout")
public class AuthController {
    private final AuthService authService;
    private final UserService userService;
    private final UserRepository userRepository;
    private final VerificationRepository verificationRepository;
    private final TotpService totpService;
    private final VerifyCookieHelper verifyCookieHelper;

    public AuthController(AuthService authService, UserService userService, UserRepository userRepository,
                          VerificationRepository verificationRepository, TotpService totpService,
                          VerifyCookieHelper verifyCookieHelper) {
        this.authService = authService;
        this.userService = userService;
        this.userRepository = userRepository;
        this.verificationRepository = verificationRepository;
        this.totpService = totpService;
        this.verifyCookieHelper = verifyCookieHelper;
    }

    @PostMapping("/login")
    ResponseEntity<AuthUserResponse> login(@RequestBody LoginRequest userRequest, HttpServletRequest request,
                                           HttpServletResponse response) {
        authService.authenticate(userRequest, request, response);
        return ResponseEntity.ok(
                new AuthUserResponse(userService.findByUsername(userRequest.username(), AuthUserDto.class)));
    }

    @PostMapping("/logout")
    ResponseEntity<Void> logout(HttpServletRequest request) {
        authService.logout(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/onboarding")
    ResponseEntity<OnboardingResponse> getOnboardEmail(HttpServletRequest request) {
        Optional<String> email = verifyCookieHelper.getVerifyCookieValue(request, VerifyCookieName.ONBOARDING_EMAIL);
        if (email.isEmpty() || email.get().isBlank()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(new OnboardingResponse(email.get()));
    }

    @PostMapping("/onboarding")
    ResponseEntity<AuthUserResponse> onboard(@RequestBody OnboardingRequest onboardingRequest,
                                             HttpServletRequest request, HttpServletResponse response) {
        Optional<String> email = verifyCookieHelper.getVerifyCookieValue(request, VerifyCookieName.ONBOARDING_EMAIL);
        if (email.isEmpty() || email.get().isBlank()) {
            return ResponseEntity.status(403).build();
        }
        if (userRepository.existsByUsernameIgnoreCase(onboardingRequest.username())) {
            throw new EntityAlreadyExistsException("user", "username");
        }
        authService.onboard(onboardingRequest);
        authService.authenticate(new LoginRequest(onboardingRequest.username(), onboardingRequest.password(), false),
                                 request, response);
        verifyCookieHelper.clearVerifyCookies(request, response);
        return ResponseEntity.ok(
                new AuthUserResponse(userService.findByUsername(onboardingRequest.username(), AuthUserDto.class)));
    }

    @PostMapping("/signup")
    ResponseEntity<Void> signup(@RequestBody SignupRequest signupRequest) throws CodeGenerationException {
        if (userRepository.existsByEmail(signupRequest.email())) {
            throw new EntityAlreadyExistsException("user", "email");
        }
        // TODO: setup resend api in prod
        Resend resend = new Resend("re_123456");
        var codeGenerationResult = totpService.generate();
        verificationRepository.save(
                new Verification(codeGenerationResult.secret(), VerifyType.ONBOARDING, signupRequest.email()));
        CreateEmailOptions params = CreateEmailOptions.builder().from("onboarding@resend.dev")
                                                      .to("orlandodiazconde" + "@gmail.com").subject("it works!")
                                                      .html("<strong>" + codeGenerationResult.code() + "</strong>")
                                                      .build();
        //        try {
        //            CreateEmailResponse data = resend.emails().send(params);
        //        } catch (ResendException e) {
        //            e.printStackTrace();
        //        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("/reset-password")
    ResponseEntity<ResetPasswordResponse> getResetPasswordUsername(HttpServletRequest request) {
        Optional<String> username = verifyCookieHelper.getVerifyCookieValue(request,
                                                                            VerifyCookieName.RESET_PASSWORD_USERNAME);
        if (username.isEmpty() || username.get().isBlank()) {
            return ResponseEntity.status(403).build();
        }

        return ResponseEntity.ok(new ResetPasswordResponse(username.get()));
    }

    @PostMapping("/reset-password")
    ResponseEntity<AuthUserResponse> resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest,
                                                   HttpServletRequest request, HttpServletResponse response) {

        Optional<String> username = verifyCookieHelper.getVerifyCookieValue(request,
                                                                            VerifyCookieName.RESET_PASSWORD_USERNAME);
        if (username.isEmpty() || username.get().isBlank()) {
            return ResponseEntity.status(403).build();
        }
        User user = userService.findByUsername(username.get());
        authService.updatePassword(user, resetPasswordRequest.password());
        verifyCookieHelper.clearVerifyCookies(request, response);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/forgot-password")
    ResponseEntity<Void> forgotPassword(@RequestBody ForgotPasswordRequest forgotPasswordRequest) {
        String query = forgotPasswordRequest.usernameOrEmail();
        User user = userRepository.findByUsernameOrEmail(query, query).orElseThrow(
                () -> new EntityNotFoundException("user", forgotPasswordRequest.usernameOrEmail()));
        var codeGenerationResult = totpService.generate();
        System.out.println(codeGenerationResult.code());
        verificationRepository.save(
                new Verification(codeGenerationResult.secret(), VerifyType.RESET_PASSWORD, user.getUsername()));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/verify")
    ResponseEntity<ProblemDetail> verify(VerifyRequestParams verifyRequest, HttpServletRequest request,
                                         HttpServletResponse response, Authentication authentication) {
        Optional<Verification> verificationOptional = verificationRepository.findByTypeAndTarget(verifyRequest.type(),
                                                                                                 verifyRequest.target());

        if (verificationOptional.isEmpty() || !totpService.isValidCode(verificationOptional.get().getSecret(),
                                                                       verifyRequest.code())) {
            return ResponseEntity.badRequest()
                                 .body(ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Invalid code."));
        }
        Verification verification = verificationOptional.get();
        verificationRepository.delete(verification);
        VerifyType storedVerifyType = verification.getType();
        switch (storedVerifyType) {
            case ONBOARDING -> verifyCookieHelper.addVerifyCookie(response, VerifyCookieName.ONBOARDING_EMAIL,
                                                                  verifyRequest.target());
            case RESET_PASSWORD -> {
                Optional<User> user = userRepository.findByUsername(verifyRequest.target(), User.class);
                if (user.isEmpty()) {
                    return ResponseEntity.badRequest().body(ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
                                                                                             "Invalid code."));
                }
                verifyCookieHelper.addVerifyCookie(response, VerifyCookieName.RESET_PASSWORD_USERNAME,
                                                   user.get().getUsername());
            }
            case CHANGE_EMAIL -> {
                Optional<String> newEmail = verifyCookieHelper.getVerifyCookieValue(request,
                                                                                    VerifyCookieName.NEW_EMAIL_ADDRESS);
                if (newEmail.isEmpty()) {
                    return ResponseEntity.badRequest().body(ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
                                                                                             "You must submit the " + "code " + "on" + " the same " + "device " + "that " + "requested the " + "email change."));
                }
                User user = userService.findByUsername(authentication.getName());
                user.setEmail(newEmail.get());
                userRepository.save(user);
                verifyCookieHelper.clearVerifyCookies(request, response);
            }
        }

        return ResponseEntity.ok().build();
    }
}
