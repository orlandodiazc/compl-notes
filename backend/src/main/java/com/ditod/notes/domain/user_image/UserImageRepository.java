package com.ditod.notes.domain.user_image;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserImageRepository extends JpaRepository<UserImage, UUID> {
}
