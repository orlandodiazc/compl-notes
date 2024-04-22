package com.ditod.notes.domain.note;

import com.ditod.notes.domain.note.dto.NoteRequest;
import com.ditod.notes.domain.note.dto.NoteSummaryResponse;
import com.ditod.notes.domain.note.dto.NoteUsernameAndIdResponse;
import com.ditod.notes.domain.note_image.NoteImage;
import com.ditod.notes.domain.note_image.NoteImageRepository;
import com.ditod.notes.domain.user.User;
import com.ditod.notes.domain.user.UserService;
import com.ditod.notes.domain.user.dto.UserNotesResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

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
    ResponseEntity<NoteSummaryResponse> oneNote(@PathVariable UUID noteId) {
        return ResponseEntity.ok(noteService.findNoteSummaryById(noteId));
    }

    @PostMapping
    ResponseEntity<NoteUsernameAndIdResponse> newNote(
            @Valid @ModelAttribute NoteRequest note,
            @PathVariable String username, HttpServletRequest request) {
        User user = userService.findByUsername(username, User.class);
        Note savedNote = noteService.save(new Note(note.getTitle(), note.getContent(), user));
        List<NoteImage> images = noteService.convertMultipartFilesToNoteImage(note.getImages(), savedNote);
        noteImageRepository.saveAll(images);
        return ResponseEntity.ok(new NoteUsernameAndIdResponse(savedNote.getId(), savedNote.getOwner()
                .getUsername()));
    }

    @DeleteMapping("/{noteId}")
    ResponseEntity<Void> deleteNote(@PathVariable UUID noteId,
            @PathVariable String username) {
        // Path variable must check for authorization
        noteService.deleteById(noteId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{noteId}")
    ResponseEntity<NoteUsernameAndIdResponse> replaceNote(
            @Valid @ModelAttribute NoteRequest newNote,
            @PathVariable UUID noteId, @PathVariable String username) {
        User owner = userService.findByUsername(username, User.class);

        Note replacedOrNewNote = noteService.findNoteById(noteId).map(note -> {
            note.setTitle(newNote.getTitle());
            note.setContent(newNote.getContent());
            List<NoteImage> imageUpdates = noteService.convertMultipartFilesToNoteImage(newNote.getImages(), note);
            if (imageUpdates.isEmpty()) {
                noteImageRepository.deleteByNote(note);
            } else {
                noteImageRepository.deleteByIdNotIn(imageUpdates.stream()
                        .map(NoteImage::getId)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList()));
            }
            noteImageRepository.saveAll(imageUpdates);
            return noteService.save(note);
        }).orElseGet(() -> {
            Note createdNote = noteService.save(new Note(newNote.getTitle(), newNote.getContent(), owner));
            createdNote.setImages(noteService.convertMultipartFilesToNoteImage(newNote.getImages(), createdNote));
            return noteService.save(createdNote);
        });

        return ResponseEntity.ok(new NoteUsernameAndIdResponse(replacedOrNewNote.getId(), replacedOrNewNote.getOwner()
                .getUsername()));
    }
}