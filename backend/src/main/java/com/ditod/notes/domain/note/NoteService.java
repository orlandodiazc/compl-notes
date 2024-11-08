package com.ditod.notes.domain.note;

import com.ditod.notes.domain.exception.EntityNotFoundException;
import com.ditod.notes.domain.note_image.NoteImage;
import com.ditod.notes.domain.note_image.NoteImageRepository;
import com.ditod.notes.domain.user.User;
import com.ditod.notes.domain.user.UserRepository;
import com.ditod.notes.web.note.dto.NoteRequest;
import com.ditod.notes.web.note.dto.NoteSummaryResponse;
import com.ditod.notes.web.note_image.dto.NoteImageRequest;
import com.ditod.notes.web.user.dto.UserNotesResponse;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class NoteService {

    private final UserRepository userRepository;
    private final NoteRepository noteRepository;
    private final NoteImageRepository noteImageRepository;

    public NoteService(UserRepository userRepository, NoteRepository noteRepository,
                       NoteImageRepository noteImageRepository) {
        this.userRepository = userRepository;
        this.noteRepository = noteRepository;
        this.noteImageRepository = noteImageRepository;
    }

    public UserNotesResponse findAll(String username) {
        return userRepository.findByUsernameIgnoreCase(username, UserNotesResponse.class)
                             .orElseThrow(() -> new EntityNotFoundException("username", username));
    }

    public NoteSummaryResponse findNoteSummaryById(UUID noteId) {
        return noteRepository.findById(noteId, NoteSummaryResponse.class)
                             .orElseThrow(() -> new EntityNotFoundException("note", noteId));
    }

    Optional<Note> findNoteById(UUID noteId) {
        return noteRepository.findById(noteId, Note.class);
    }

    public Note save(Note note) {
        return noteRepository.save(note);
    }

    public void deleteById(UUID noteId) {
        noteRepository.deleteById(noteId);
    }

    public Note updateNote(NoteRequest newNote, UUID noteId, User owner) {
        return this.findNoteById(noteId).map(note -> {
            note.setTitle(newNote.getTitle());
            note.setContent(newNote.getContent());
            List<NoteImage> imageUpdates = this.convertMultipartFilesToNoteImage(newNote.getImages(), note);
            if (imageUpdates.isEmpty()) {
                noteImageRepository.deleteByNote(note);
            } else {
                noteImageRepository.deleteByIdNotIn(
                        imageUpdates.stream().map(NoteImage::getId).collect(Collectors.toList()));
                noteImageRepository.saveAll(imageUpdates);
            }
            return this.save(note);
        }).orElseGet(() -> {
            Note createdNote = this.save(new Note(newNote.getTitle(), newNote.getContent(), owner));
            createdNote.setImages(this.convertMultipartFilesToNoteImage(newNote.getImages(), createdNote));
            return this.save(createdNote);
        });
    }

    public List<NoteImage> convertMultipartFilesToNoteImage(List<NoteImageRequest> images, Note note) {
        return Optional.ofNullable(images).orElse(new ArrayList<>())  // Use an empty list if images is null
                       .stream().map(image -> processNoteImage(image, note)).filter(Objects::nonNull).toList();
    }

    private NoteImage processNoteImage(NoteImageRequest requestImage, Note note) {
        try {
            if (requestImage.getId() != null) {
                System.out.println("updated");

                return updateNoteImage(requestImage);
            } else if (requestImage.getFile() != null && !requestImage.getFile().isEmpty()) {
                System.out.println("created");

                return createNoteImage(requestImage, note);
            }
            return null;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private NoteImage updateNoteImage(NoteImageRequest requestImage) throws IOException {
        NoteImage noteImageToUpdate = noteImageRepository.findById(requestImage.getId()).orElseThrow(
                () -> new EntityNotFoundException("image", requestImage.getId()));
        noteImageToUpdate.setAltText(requestImage.getAltText());
        if (requestImage.getFile() != null && !requestImage.getFile().isEmpty()) {
            noteImageToUpdate.setContentType(requestImage.getFile().getContentType());
            noteImageToUpdate.setBlob(requestImage.getFile().getBytes());
        }
        return noteImageToUpdate;
    }

    private NoteImage createNoteImage(NoteImageRequest requestImage, Note note) throws IOException {
        return new NoteImage(requestImage.getAltText(), requestImage.getFile().getContentType(),
                             requestImage.getFile().getBytes(), note);
    }
}
