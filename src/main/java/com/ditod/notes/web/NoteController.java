package com.ditod.notes.web;

import com.ditod.notes.domain.note.Note;
import com.ditod.notes.domain.note.NoteService;
import com.ditod.notes.domain.note.dto.NoteRequest;
import com.ditod.notes.domain.note.dto.NoteSummaryResponse;
import com.ditod.notes.domain.note_image.NoteImage;
import com.ditod.notes.domain.note_image.NoteImageRepository;
import com.ditod.notes.domain.user.User;
import com.ditod.notes.domain.user.UserService;
import com.ditod.notes.domain.user.dto.UserNotesResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users/{username}/notes")
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

    @GetMapping
    ResponseEntity<UserNotesResponse> allNotes(@PathVariable String username) {
        return ResponseEntity.ok(noteService.findAll(username));
    }

    @GetMapping("/{noteId}")
    ResponseEntity<NoteSummaryResponse> getNote(@PathVariable UUID noteId) {
        return ResponseEntity.ok(noteService.findNoteSummaryById(noteId));
    }

    @PostMapping
    ResponseEntity<NoteSummaryResponse> createNote(
            @Valid @ModelAttribute NoteRequest note,
            @PathVariable String username) {
        User user = userService.findByUsername(username);
        Note savedNote = noteService.save(new Note(note.getTitle(), note.getContent(), user));
        List<NoteImage> images = noteService.convertMultipartFilesToNoteImage(note.getImages(), savedNote);
        noteImageRepository.saveAll(images);
        return ResponseEntity.ok(new NoteSummaryResponse(savedNote.getId(), savedNote.getTitle(), savedNote.getContent(), new NoteSummaryResponse.OwnerSummary(savedNote.getOwner()
                .getId()), savedNote.getCreatedAt(), savedNote.getUpdatedAt(), savedNote.getImages()));
    }

    @DeleteMapping("/{noteId}")
    ResponseEntity<Void> deleteNote(@PathVariable String username,
            @PathVariable UUID noteId) {
        userService.findByUsername(username);
        noteService.deleteById(noteId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{noteId}")
    ResponseEntity<NoteSummaryResponse> updateNote(
            @Valid @ModelAttribute NoteRequest newNote,
            @PathVariable String username, @PathVariable UUID noteId) {
        User owner = userService.findByUsername(username);

        Note replacedOrNewNote = noteService.updateNote(newNote, noteId, owner);

        return ResponseEntity.ok(new NoteSummaryResponse(replacedOrNewNote.getId(), replacedOrNewNote.getTitle(), replacedOrNewNote.getContent(), new NoteSummaryResponse.OwnerSummary(replacedOrNewNote.getOwner()
                .getId()), replacedOrNewNote.getCreatedAt(), replacedOrNewNote.getUpdatedAt(), replacedOrNewNote.getImages()));
    }
}