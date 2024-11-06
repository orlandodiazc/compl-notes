package com.ditod.notes.web.user.dto;

import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public interface UserNotesResponse extends UserBaseDto {
    @NotNull
    List<NoteSummary> getNotes();

    interface NoteSummary {
        @NotNull
        UUID getId();

        @NotNull
        String getTitle();
    }
}
