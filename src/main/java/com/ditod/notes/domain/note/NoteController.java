package com.ditod.notes.domain.note;

import com.ditod.notes.domain.user.User;
import com.ditod.notes.domain.user.UserNotesProjection;
import com.ditod.notes.domain.user.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@RestController
@RequestMapping("/users/{username}/notes")
public class NoteController {
    private final UserRepository userRepository;
    private final NoteRepository noteRepository;

    public NoteController(UserRepository userRepository, NoteRepository noteRepository) {
        this.userRepository = userRepository;
        this.noteRepository = noteRepository;
    }

    @GetMapping
    UserNotesProjection all(@PathVariable String username) {
        return userRepository.findByUsername(username, UserNotesProjection.class)
                .orElseThrow(() -> new UsernameNotFoundException(username));
    }

    @GetMapping("/{id}")
    NoteSummaryProjection one(@PathVariable UUID id) {
        return noteRepository.findById(id, NoteSummaryProjection.class)
                .orElseThrow();
    }

    @PostMapping
    NoteUsernameAndIdResponse newNote(@RequestBody Note note) {
        User user = userRepository.findById(note.getOwner().getId())
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found"));
        Note savedNote = noteRepository.save(new Note(note.getTitle(), note.getContent(), user, note.getImages()));
        System.out.println(savedNote);
        return new NoteUsernameAndIdResponse(savedNote.getId(), user.getUsername());
    }

    @DeleteMapping("/{id}")
    void deleteNote(@PathVariable UUID id) {
        noteRepository.deleteById(id);
    }

    @PutMapping("/{id}")
    NoteUsernameAndIdResponse replaceNote(@RequestBody Note newNote, @PathVariable UUID id) {
        Note replacedNote = noteRepository.findById(id).map(note -> {
            note.setTitle(newNote.getTitle());
            note.setContent(newNote.getContent());
            note.setImages(newNote.getImages());
            return noteRepository.save(note);
        }).orElseGet(() -> noteRepository.save(newNote));

        return new NoteUsernameAndIdResponse(replacedNote.getId(), replacedNote.getOwner()
                .getUsername());
    }
}