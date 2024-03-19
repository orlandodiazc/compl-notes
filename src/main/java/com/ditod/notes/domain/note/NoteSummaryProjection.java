package com.ditod.notes.domain.note;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface NoteSummaryProjection {
    UUID getId();

    String getTitle();

    String getContent();

    OwnerSummary getOwner();

    Instant getUpdatedAt();

    List<ImageSummary> getImages();

    interface OwnerSummary {
        UUID getId();
    }

    interface ImageSummary {
        UUID getId();

        String getAltText();
    }
}
