package com.ditod.notes.domain.note_image;

import com.ditod.notes.domain.note.Note;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class NoteImageService {
    private final NoteImageRepository noteImageRepository;

    public NoteImageService(NoteImageRepository noteImageRepository) {
        this.noteImageRepository = noteImageRepository;
    }

    public void deleteByNote(Note note) {
        noteImageRepository.deleteByNote(note);
    }

    public void deleteByIdNotIn(List<UUID> ids) {
        noteImageRepository.deleteByIdNotIn(ids);
    }
}
