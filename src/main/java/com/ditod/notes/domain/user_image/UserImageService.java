package com.ditod.notes.domain.user_image;

import com.ditod.notes.domain.exception.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserImageService {

    private final UserImageRepository userImageRepository;

    public UserImageService(UserImageRepository userImageRepository) {
        this.userImageRepository = userImageRepository;
    }

    public UserImage findById(UUID id) {
        return userImageRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("No user image with the id " + id + " exists"));
    }
}
