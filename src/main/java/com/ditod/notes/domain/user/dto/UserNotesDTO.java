package com.ditod.notes.domain.user.dto;


import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public interface UserNotesDTO extends UserBaseDTO {
    @NotNull List<NoteSummary> getNotes();

    interface NoteSummary {
        @NotNull UUID getId();

        String getTitle();
    }

}
