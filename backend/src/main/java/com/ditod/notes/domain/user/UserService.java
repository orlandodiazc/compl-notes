package com.ditod.notes.domain.user;

import com.ditod.notes.domain.exception.UserDoesNotExistException;
import com.ditod.notes.web.user.dto.UserFilteredResponse;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
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

    public void save(User user) {
        userRepository.save(user);
    }
}
