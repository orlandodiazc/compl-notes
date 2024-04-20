package com.ditod.notes.domain.note;

import com.ditod.notes.domain.exception.EntityNotFoundException;
import com.ditod.notes.domain.note.dto.NoteImageRequest;
import com.ditod.notes.domain.note.dto.NoteSummaryResponse;
import com.ditod.notes.domain.note_image.NoteImage;
import com.ditod.notes.domain.user.UserRepository;
import com.ditod.notes.domain.user.dto.UserNotesResponse;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;

@Service
public class NoteService {

    private final UserRepository userRepository;
    private final NoteRepository noteRepository;

    public NoteService(UserRepository userRepository,
            NoteRepository noteRepository) {
        this.userRepository = userRepository;
        this.noteRepository = noteRepository;
    }

    public List<NoteImage> convertMultipartFilesToNoteImage(
            List<NoteImageRequest> images, Note note) {

        return Optional.ofNullable(images)
                .map(Collection::stream)
                .orElseGet(Stream::empty)
                .map(i -> {
                    try {
                        if (i.getFile() != null && !i.getFile().isEmpty()) {
                            if (i.getId() != null) {
                                return new NoteImage(i.getId(), i.getAltText(), i.getFile()
                                        .getContentType(), i.getFile()
                                        .getBytes(), note);
                            } else {
                                return new NoteImage(i.getAltText(), i.getFile()
                                        .getContentType(), i.getFile()
                                        .getBytes(), note);
                            }
                        } else if (i.getId() != null) {
                            return new NoteImage(i.getId(), i.getAltText(), note);
                        }
                        return null;

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }).filter(Objects::nonNull)
                .toList();
    }

    UserNotesResponse findAll(String username) {
        return userRepository.findByUsername(username, UserNotesResponse.class)
                .orElseThrow(() -> new EntityNotFoundException("username", username));
    }

    NoteSummaryResponse findNoteSummaryById(UUID noteId) {
        return noteRepository.findById(noteId, NoteSummaryResponse.class)
                .orElseThrow(() -> new EntityNotFoundException("note", noteId));
    }

    Optional<Note> findNoteById(UUID noteId) {
        return noteRepository.findById(noteId, Note.class);
        // fix note not found
    }

    public Note save(Note note) {
        return noteRepository.save(note);
    }

    public void deleteById(UUID noteId) {
        noteRepository.deleteById(noteId);
    }

}
