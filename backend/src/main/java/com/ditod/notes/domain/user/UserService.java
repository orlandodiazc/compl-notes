package com.ditod.notes.domain.user;

import com.ditod.notes.domain.exception.EntityNotFoundException;
import com.ditod.notes.web.user.dto.UserFilteredResponse;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User findById(UUID id) {
        return userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("username", id));
    }

    public List<UserFilteredResponse> findFilteredUsers(String search) {
        Pageable pageable = PageRequest.of(0, 6);
        return userRepository.findFilteredUsers(search, pageable);
    }

    public <T> T findByUsername(String username, Class<T> type) {
        return userRepository.findByUsername(username, type)
                             .orElseThrow(() -> new EntityNotFoundException("username", username));
    }

    //    public <T> Optional<T> findByUsername(String username, Class<T> type) {
    //        return userRepository.findByUsername(username, type);
    //    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username, User.class)
                             .orElseThrow(() -> new EntityNotFoundException("username", username));
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsernameIgnoreCase(username);
    }
}
