package com.ditod.notes.domain.user.dto;


import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public interface UserNotesResponse extends UserBaseResponse {
    @NotNull List<NoteSummary> getNotes();

    interface NoteSummary {
        @NotNull UUID getId();

        @NotNull String getTitle();
    }

}
