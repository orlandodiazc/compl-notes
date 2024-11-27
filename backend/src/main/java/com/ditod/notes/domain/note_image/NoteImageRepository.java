package com.ditod.notes.domain.note_image;

import com.ditod.notes.domain.note.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface NoteImageRepository extends JpaRepository<NoteImage, UUID> {
    @Transactional
    void deleteByNote(Note note);
    @Transactional
    void deleteByNoteAndIdNotIn(Note note, List<UUID> collect);
}
