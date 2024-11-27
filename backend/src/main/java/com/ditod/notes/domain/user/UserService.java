package com.ditod.notes.domain.user;

import com.ditod.notes.domain.exception.EntityDoesNotExistException;
import com.ditod.notes.domain.exception.UserDoesNotExistException;
import com.ditod.notes.web.user.dto.UserFilteredResponse;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User findById(UUID id) {
        return userRepository.findById(id)
                             .orElseThrow(() -> new EntityDoesNotExistException("username", id));
    }

    public List<UserFilteredResponse> findFilteredUsers(String search) {
        Pageable pageable = PageRequest.of(0, 6);
        return userRepository.findFilteredUsers(search, pageable);
    }

    public <T> T findByUsername(String username, Class<T> type) {
        return userRepository.findByUsername(username, type)
                             .orElseThrow(() -> new UserDoesNotExistException((username)));
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                             .orElseThrow(() -> new UserDoesNotExistException((username)));
    }

    public boolean existsByUsernameIgnoreCase(String username) {
        return userRepository.existsByUsernameIgnoreCase(username);
    }

    public void save(User user) {
        userRepository.save(user);
    }
}
