package com.ditod.notes;

import com.ditod.notes.domain.note.Note;
import com.ditod.notes.domain.note.NoteRepository;
import com.ditod.notes.domain.note_image.NoteImage;
import com.ditod.notes.domain.note_image.NoteImageRepository;
import com.ditod.notes.domain.user.User;
import com.ditod.notes.domain.user.UserRepository;
import com.ditod.notes.domain.user_image.UserImage;
import com.ditod.notes.domain.user_image.UserImageRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Files;

@Component
public class DataLoader implements ApplicationRunner {
    private final UserRepository userRepository;
    private final UserImageRepository userImageRepository;
    private final NoteRepository noteRepository;

    private final NoteImageRepository noteImageRepository;
    static final String IMAGES_DIRECTORY = "/home/ditod/Code/JavaProjects/notes/src/test/java/com/ditod/notes/fixtures/images";

    public DataLoader(UserRepository userRepository,
            UserImageRepository userImageRepository,
            NoteRepository noteRepository,
            NoteImageRepository noteImageRepository) {
        this.userRepository = userRepository;
        this.userImageRepository = userImageRepository;
        this.noteRepository = noteRepository;
        this.noteImageRepository = noteImageRepository;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        File ditodImageFile = new File(IMAGES_DIRECTORY + "/user/ditod.png");
        byte[] ditodImageContent = Files.readAllBytes(ditodImageFile.toPath());
        User ditod = new User("ditod@test.com", "ditod", "Orlando Diaz");
        UserImage ditodImage = new UserImage("Dito's profile picture", MediaType.IMAGE_PNG.toString(), ditodImageContent, ditod);

        Note ditodNote = new Note("Koalas", "Koalas are great", ditod);
        File ditodNoteImageFile = new File(IMAGES_DIRECTORY + "/ditod-notes/cute-koala.png");
        byte[] ditodNoteImageContent = Files.readAllBytes(ditodNoteImageFile.toPath());
        NoteImage ditodNoteImage = new NoteImage("Dito's note picture", MediaType.IMAGE_PNG.toString(), ditodNoteImageContent, ditodNote);

        userRepository.save(ditod);
        userImageRepository.save(ditodImage);
        noteRepository.save(ditodNote);
        noteImageRepository.save(ditodNoteImage);
    }
}
