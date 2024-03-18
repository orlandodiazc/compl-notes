package com.ditod.notes.domain.user;

import java.util.UUID;

public interface UserBaseProjection {
    UUID getId();

    String getUsername();

    String getName();

    UUID getImageId();
}
