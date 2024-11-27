package com.ditod.notes.security;

import com.ditod.notes.domain.exception.UserDoesNotExistException;
import com.ditod.notes.domain.user.AuthUser;
import com.ditod.notes.domain.user.UserRepository;
import com.ditod.notes.domain.user.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class JpaUserDetailsService implements UserDetailsService {

    private final UserService userService;

    public JpaUserDetailsService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return new AuthUser(userService.findByUsername(username));
    }
}