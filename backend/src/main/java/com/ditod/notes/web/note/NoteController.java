package com.ditod.notes.web.note;

import com.ditod.notes.domain.note.Note;
import com.ditod.notes.domain.note.NoteService;
import com.ditod.notes.domain.note_image.NoteImage;
import com.ditod.notes.domain.note_image.NoteImageRepository;
import com.ditod.notes.domain.user.User;
import com.ditod.notes.domain.user.UserService;
import com.ditod.notes.web.note.dto.NoteRequest;
import com.ditod.notes.web.note.dto.NoteSummaryResponse;
import com.ditod.notes.web.user.dto.UserNotesResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping()
@Tag(name = "note", description = "Access user notes")
public class NoteController {
    private final NoteService noteService;
    private final UserService userService;
    private final NoteImageRepository noteImageRepository;

    public NoteController(NoteService noteService, UserService userService,
                          NoteImageRepository noteImageRepository) {
        this.noteService = noteService;
        this.userService = userService;
        this.noteImageRepository = noteImageRepository;
    }

    @GetMapping(value = "/users/{username}/notes")
    ResponseEntity<UserNotesResponse> allNotes(@PathVariable String username) {
        return ResponseEntity.ok(noteService.findAll(username));
    }

    @GetMapping("/notes/{noteId}")
    ResponseEntity<NoteSummaryResponse> getNote(@PathVariable UUID noteId) {
        return ResponseEntity.ok(noteService.findNoteSummaryById(noteId));
    }

    @PostMapping(value = "/users/{username}/notes", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<Note> createNote(@Valid @ModelAttribute NoteRequest note,
                                    @PathVariable String username) {
        User user = userService.findByUsername(username);
        Note savedNote = noteService.save(new Note(note.getTitle(), note.getContent(), user));
        List<NoteImage> images = noteService.convertMultipartFilesToNoteImage(note.getImages(),
                                                                              savedNote);
        noteImageRepository.saveAll(images);
        return ResponseEntity.ok(savedNote);
    }

    @DeleteMapping("/notes/{noteId}")
    ResponseEntity<Void> deleteNote(@PathVariable UUID noteId) {
        noteService.deleteById(noteId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping(value = "/users/{username}/notes/{noteId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<Note> updateNote(@Valid @ModelAttribute NoteRequest newNote,
                                    @PathVariable String username, @PathVariable UUID noteId) {
        User owner = userService.findByUsername(username);
        Note replacedOrNewNote = noteService.updateNote(newNote, noteId, owner);
        return ResponseEntity.ok(replacedOrNewNote);
    }
}