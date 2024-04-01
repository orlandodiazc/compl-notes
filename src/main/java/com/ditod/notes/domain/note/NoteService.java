package com.ditod.notes.domain.note;

import com.ditod.notes.domain.exception.NoteDoesNotExistException;
import com.ditod.notes.domain.note.dto.NoteImageRequest;
import com.ditod.notes.domain.note.dto.NoteSummaryDTO;
import com.ditod.notes.domain.note_image.NoteImage;
import com.ditod.notes.domain.user.UserRepository;
import com.ditod.notes.domain.user.dto.UserNotesDTO;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
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
                        return new NoteImage(i.getId(), i.getAltText(), i.getFile()
                                .getContentType(), i.getFile()
                                .getBytes(), note);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .toList();
    }

    UserNotesDTO findAll(String username) {
        return userRepository.findByUsername(username, UserNotesDTO.class)
                .orElseThrow(() -> new UsernameNotFoundException(username)); // fix user not found
    }

    NoteSummaryDTO findNoteSummaryById(UUID noteId) {
        return noteRepository.findById(noteId, NoteSummaryDTO.class)
                .orElseThrow(() -> new NoteDoesNotExistException(noteId));
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
