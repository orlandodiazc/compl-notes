package com.ditod.notes.domain.note;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface NoteRepository extends JpaRepository<Note, UUID> {
    <T> Optional<T> findById(UUID id, Class<T> type);
}
