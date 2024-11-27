package com.ditod.notes.domain.note;

import com.ditod.notes.domain.exception.EntityDoesNotExistException;
import com.ditod.notes.domain.note_image.NoteImage;
import com.ditod.notes.domain.note_image.NoteImageRepository;
import com.ditod.notes.domain.user.User;
import com.ditod.notes.domain.user.UserService;
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

    private final NoteRepository noteRepository;
    private final NoteImageRepository noteImageRepository;
    private final UserService userService;

    public NoteService(NoteRepository noteRepository, NoteImageRepository noteImageRepository,
                       UserService userService) {
        this.noteRepository = noteRepository;
        this.noteImageRepository = noteImageRepository;
        this.userService = userService;
    }

    public UserNotesResponse findAll(String username) {
        return userService.findByUsername(username, UserNotesResponse.class);

    }

    public NoteSummaryResponse findNoteSummaryById(UUID noteId) {
        return noteRepository.findById(noteId, NoteSummaryResponse.class)
                             .orElseThrow(() -> new EntityDoesNotExistException("note", noteId));
    }

    public Note save(Note note) {
        return noteRepository.save(note);
    }

    public void deleteById(UUID noteId) {
        noteRepository.deleteById(noteId);
    }

    public Note updateNote(NoteRequest newNote, UUID noteId, User owner) {
        return noteRepository.findById(noteId, Note.class).map(note -> {
            note.setTitle(newNote.getTitle());
            note.setContent(newNote.getContent());
            List<NoteImage> imageUpdates = this.convertMultipartFilesToNoteImage(
                    newNote.getImages(), note);
            if (imageUpdates.isEmpty()) {
                noteImageRepository.deleteByNote(note);
            } else {
                noteImageRepository.deleteByNoteAndIdNotIn(note, imageUpdates.stream()
                                                                             .map(NoteImage::getId)
                                                                             .collect(
                                                                                     Collectors.toList()));
                noteImageRepository.saveAll(imageUpdates);
            }
            return this.save(note);
        }).orElseGet(() -> {
            Note createdNote = this.save(new Note(newNote.getTitle(), newNote.getContent(), owner));
            createdNote.setImages(
                    this.convertMultipartFilesToNoteImage(newNote.getImages(), createdNote));
            return this.save(createdNote);
        });
    }

    public List<NoteImage> convertMultipartFilesToNoteImage(List<NoteImageRequest> images,
                                                            Note note) {
        return Optional.ofNullable(images)
                       .orElse(new ArrayList<>())  // Use an empty list if images is null
                       .stream().map(image -> processNoteImage(image, note))
                       .filter(Objects::nonNull).toList();
    }

    private NoteImage processNoteImage(NoteImageRequest requestImage, Note note) {
        try {
            if (requestImage.getId() != null) {
                return updateNoteImage(requestImage);
            } else if (requestImage.getFile() != null && !requestImage.getFile().isEmpty()) {
                return createNoteImage(requestImage, note);
            }
            return null;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private NoteImage updateNoteImage(NoteImageRequest requestImage) throws IOException {
        NoteImage noteImageToUpdate = noteImageRepository.findById(requestImage.getId())
                                                         .orElseThrow(
                                                                 () -> new EntityDoesNotExistException(
                                                                         "image",
                                                                         requestImage.getId()));
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
