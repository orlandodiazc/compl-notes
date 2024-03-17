package com.ditod.notes.domain.note_image;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface NoteImageRepository extends JpaRepository<NoteImage, UUID> {
}
