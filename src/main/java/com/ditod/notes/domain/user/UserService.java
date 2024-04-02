package com.ditod.notes.domain.user;

import com.ditod.notes.domain.exception.UsernameDoesNotExistException;
import com.ditod.notes.domain.user.dto.UserFilteredResponse;
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
        return userRepository.findById(id)
                .orElseThrow(() -> new UsernameDoesNotExistException(id));
    }

    public List<UserFilteredResponse> findFilteredUsers(String search) {
        Pageable pageable = PageRequest.of(0, 6);
        return userRepository.findFilteredUsers(search, pageable);
    }

    public <T> T findByUsername(String username, Class<T> type) {
        return userRepository.findByUsername(username, type)
                .orElseThrow(() -> new UsernameDoesNotExistException(username));
    }
}
