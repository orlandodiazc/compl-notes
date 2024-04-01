package com.ditod.notes.domain.note;

import com.ditod.notes.domain.note.dto.NoteRequest;
import com.ditod.notes.domain.note.dto.NoteSummaryDTO;
import com.ditod.notes.domain.note.dto.NoteUsernameAndIdDTO;
import com.ditod.notes.domain.note_image.NoteImage;
import com.ditod.notes.domain.note_image.NoteImageRepository;
import com.ditod.notes.domain.user.User;
import com.ditod.notes.domain.user.UserRepository;
import com.ditod.notes.domain.user.UserService;
import com.ditod.notes.domain.user.dto.UserNotesDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users/{username}/notes")
public class NoteController {
    private final NoteService noteService;
    private final UserRepository userRepository;
    private final UserService userService;
    private final NoteImageRepository noteImageRepository;

    private final NoteRepository noteRepository;

    public NoteController(NoteService noteService,
            UserRepository userRepository, UserService userService,
            NoteImageRepository noteImageRepository,
            NoteRepository noteRepository) {
        this.noteService = noteService;
        this.userRepository = userRepository;
        this.userService = userService;
        this.noteImageRepository = noteImageRepository;
        this.noteRepository = noteRepository;
    }

    @GetMapping
    UserNotesDTO allNotes(@PathVariable String username) {
        return userRepository.findByUsername(username, UserNotesDTO.class)
                .orElseThrow(() -> new UsernameNotFoundException(username)); // fix user not found
    }

    @GetMapping("/{noteId}")
    NoteSummaryDTO oneNote(@PathVariable UUID noteId) {
        return noteRepository.findById(noteId, NoteSummaryDTO.class)
                .orElseThrow();
    }

    @PostMapping
    NoteUsernameAndIdDTO newNote(@RequestBody Note note) {
        User user = userService.findById(note.getOwner().getId());
        Note savedNote = noteRepository.save(new Note(note.getTitle(), note.getContent(), user, note.getImages()));
        return new NoteUsernameAndIdDTO(savedNote.getId(), user.getUsername());
    }

    @DeleteMapping("/{noteId}")
    void deleteNote(@PathVariable UUID noteId, @PathVariable String username) {
        noteRepository.deleteById(noteId);
    }

    @PutMapping("/{noteId}")
    ResponseEntity<NoteUsernameAndIdDTO> replaceNote(
            @ModelAttribute NoteRequest newNote, @PathVariable UUID noteId,
            @PathVariable String username) {
        User owner = userService.findByUsername(username, User.class);

        Note replacedNote = noteRepository.findById(noteId).map(note -> {
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
            return noteRepository.save(note);
        }).orElseGet(() -> {
            Note createdNote = noteRepository.save(new Note(newNote.getTitle(), newNote.getContent(), owner));
            createdNote.setImages(noteService.convertMultipartFilesToNoteImage(newNote.getImages(), createdNote));
            return noteRepository.save(createdNote);
        });

        return ResponseEntity.ok(new NoteUsernameAndIdDTO(replacedNote.getId(), replacedNote.getOwner()
                .getUsername()));
    }
}