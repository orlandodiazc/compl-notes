package com.ditod.notes.auth;

import com.ditod.notes.domain.exception.EntityNotFoundException;
import com.ditod.notes.domain.user.User;
import com.ditod.notes.domain.user.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class JpaUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public JpaUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsernameIgnoreCase(username, User.class)
                             .orElseThrow(() -> new EntityNotFoundException("username", username));
    }
}