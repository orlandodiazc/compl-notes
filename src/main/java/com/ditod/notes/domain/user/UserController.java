package com.ditod.notes.domain.user;

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
    ResponseEntity<List<UserFilteredResponse>> filteredUsers(
            @RequestParam(required = false, defaultValue = "") String search) {
        return ResponseEntity.ok(userService.findFilteredUsers(search));
    }

    @GetMapping("/{username}")
    ResponseEntity<UserSummaryResponse> onUser(@PathVariable String username) {
        return ResponseEntity.ok(userService.findByUsername(username, UserSummaryResponse.class));

    }

}
