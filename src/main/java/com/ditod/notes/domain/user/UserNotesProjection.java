package com.ditod.notes.domain.user;


import java.util.List;
import java.util.UUID;

public interface UserNotesProjection extends UserBaseProjection {
    List<NoteSummary> getNotes();

    interface NoteSummary {
        UUID getId();

        String getTitle();
    }

}
