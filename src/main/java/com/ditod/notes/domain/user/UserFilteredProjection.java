package com.ditod.notes.domain.user;


import java.util.UUID;

public interface UserFilteredProjection {
    UUID getId();

    String getUsername();

    String getName();

    UUID getImageId();
}

