package com.ditod.notes.domain.user;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    List<UserFilteredProjection> filteredInvoices(@RequestParam(required = false, defaultValue = "") String query) {
        Pageable pageable = PageRequest.of(0, 6);
        return userRepository.findFilteredUsers(query, pageable);
    }

    @GetMapping("/{username}")
    UserSummaryProjection one(@PathVariable String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found"));
    }

}
