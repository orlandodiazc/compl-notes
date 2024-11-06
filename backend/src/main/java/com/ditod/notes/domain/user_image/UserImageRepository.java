package com.ditod.notes.domain.user_image;

import com.ditod.notes.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserImageRepository extends JpaRepository<UserImage, UUID> {
    Optional<UserImage> findByUser(User user);
}
