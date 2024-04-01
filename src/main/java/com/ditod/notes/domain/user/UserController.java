package com.ditod.notes.domain.user;

import com.ditod.notes.domain.user.dto.UserFilteredDTO;
import com.ditod.notes.domain.user.dto.UserSummaryDTO;
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
    ResponseEntity<List<UserFilteredDTO>> filteredUsers(
            @RequestParam(required = false, defaultValue = "") String search) {
        return ResponseEntity.ok(userService.findFilteredUsers(search));
    }

    @GetMapping("/{username}")
    ResponseEntity<UserSummaryDTO> onUser(@PathVariable String username) {
        return ResponseEntity.ok(userService.findByUsername(username, UserSummaryDTO.class));

    }

}
