package com.ditod.notes.domain.note;

import com.ditod.notes.domain.note.dto.NoteRequest;
import com.ditod.notes.domain.note.dto.NoteSummaryDTO;
import com.ditod.notes.domain.note.dto.NoteUsernameAndIdDTO;
import com.ditod.notes.domain.note_image.NoteImage;
import com.ditod.notes.domain.note_image.NoteImageService;
import com.ditod.notes.domain.user.User;
import com.ditod.notes.domain.user.UserService;
import com.ditod.notes.domain.user.dto.UserNotesDTO;
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
    private final NoteImageService noteImageService;

    public NoteController(NoteService noteService, UserService userService,
            NoteImageService noteImageService) {
        this.noteService = noteService;
        this.userService = userService;
        this.noteImageService = noteImageService;
    }

    @GetMapping
    UserNotesDTO allNotes(@PathVariable String username) {
        return noteService.findAll(username);
    }

    @GetMapping("/{noteId}")
    NoteSummaryDTO oneNote(@PathVariable UUID noteId) {
        return noteService.findNoteSummaryById(noteId);
    }

    @PostMapping
    NoteUsernameAndIdDTO newNote(@RequestBody Note note) {
        User user = userService.findById(note.getOwner().getId());
        Note savedNote = noteService.save(new Note(note.getTitle(), note.getContent(), user, note.getImages()));
        return new NoteUsernameAndIdDTO(savedNote.getId(), user.getUsername());
    }

    @DeleteMapping("/{noteId}")
    void deleteNote(@PathVariable UUID noteId, @PathVariable String username) {
        // Path variable must check for authorization
        noteService.deleteById(noteId);
    }

    @PutMapping("/{noteId}")
    ResponseEntity<NoteUsernameAndIdDTO> replaceNote(
            @ModelAttribute NoteRequest newNote, @PathVariable UUID noteId,
            @PathVariable String username) {
        User owner = userService.findByUsername(username, User.class);

        Note replacedOrNewNote = noteService.findNoteById(noteId).map(note -> {
            note.setTitle(newNote.getTitle());
            note.setContent(newNote.getContent());
            List<NoteImage> imageUpdates = noteService.convertMultipartFilesToNoteImage(newNote.getImages(), note);
            if (imageUpdates.isEmpty()) {
                noteImageService.deleteByNote(note);
            } else {
                noteImageService.deleteByIdNotIn(imageUpdates.stream()
                        .map(NoteImage::getId)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList()));
            }

            return noteService.save(note);
        }).orElseGet(() -> {
            Note createdNote = noteService.save(new Note(newNote.getTitle(), newNote.getContent(), owner));
            createdNote.setImages(noteService.convertMultipartFilesToNoteImage(newNote.getImages(), createdNote));
            return noteService.save(createdNote);
        });

        return ResponseEntity.ok(new NoteUsernameAndIdDTO(replacedOrNewNote.getId(), replacedOrNewNote.getOwner()
                .getUsername()));
    }
}