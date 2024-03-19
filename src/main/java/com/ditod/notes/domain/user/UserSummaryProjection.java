package com.ditod.notes.domain.user;

import java.time.Instant;

public interface UserSummaryProjection extends UserBaseProjection {
    Instant getCreatedAt();
}
