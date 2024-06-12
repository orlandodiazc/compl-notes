package com.ditod.notes.web;

import com.ditod.notes.domain.user.UserService;
import com.ditod.notes.domain.user.dto.UserFilteredResponse;
import com.ditod.notes.domain.user.dto.UserSummaryResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
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

}
