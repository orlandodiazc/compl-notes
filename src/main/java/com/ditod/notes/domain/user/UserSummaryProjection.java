package com.ditod.notes.domain.user;

import java.time.LocalDateTime;

public interface UserSummaryProjection extends UserBaseProjection {
    LocalDateTime getCreatedAt();
}
