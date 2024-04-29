package com.ditod.notes;

import com.ditod.notes.domain.note.Note;
import com.ditod.notes.domain.note.NoteRepository;
import com.ditod.notes.domain.note_image.NoteImage;
import com.ditod.notes.domain.note_image.NoteImageRepository;
import com.ditod.notes.domain.permission.Permission;
import com.ditod.notes.domain.permission.PermissionRepository;
import com.ditod.notes.domain.role.Role;
import com.ditod.notes.domain.role.RoleRepository;
import com.ditod.notes.domain.user.User;
import com.ditod.notes.domain.user.UserRepository;
import com.ditod.notes.domain.user_image.UserImage;
import com.ditod.notes.domain.user_image.UserImageRepository;
import net.datafaker.Faker;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

@Profile("dev")
@Component
public class DataLoader implements ApplicationRunner {
    static final String IMAGES_DIRECTORY = "src/test/java/com/ditod/notes/fixtures/images";
    private final UserRepository userRepository;
    private final UserImageRepository userImageRepository;
    private final NoteRepository noteRepository;
    private final NoteImageRepository noteImageRepository;
    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public DataLoader(UserRepository userRepository,
            UserImageRepository userImageRepository,
            NoteRepository noteRepository,
            NoteImageRepository noteImageRepository,
            PermissionRepository permissionRepository,
            RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userImageRepository = userImageRepository;
        this.noteRepository = noteRepository;
        this.noteImageRepository = noteImageRepository;
        this.permissionRepository = permissionRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    private byte[] readFileBytes(File file) {
        try {
            return Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        List<String> entities = List.of("USER", "NOTE");
        List<String> actions = List.of("CREATE", "READ", "UPDATE", "DELETE");
        List<String> accesses = List.of("OWN", "ANY");


        for (String entity : entities) {
            for (String action : actions) {
                for (String access : accesses) {
                    permissionRepository.save(new Permission(action, entity, access));
                }
            }
        }

        List<Permission> permissionsAllAccess = permissionRepository.findAll();
        List<Permission> permissionsOwnAccess = permissionRepository.findAllByAccess("OWN");
        Role adminRole = new Role("ROLE_ADMIN", permissionsAllAccess);
        Role userRole = new Role("ROLE_USER", permissionsOwnAccess);
        roleRepository.save(adminRole);
        roleRepository.save(userRole);


        List<ImageFile> noteImagesFile = List.of(new ImageFile("a nice country house", new File(IMAGES_DIRECTORY + "/notes/0.png")), new ImageFile("a city scape", new File(IMAGES_DIRECTORY + "/notes/1.png")), new ImageFile("a sunrise", new File(IMAGES_DIRECTORY + "/notes/2.png")));
        List<ImageBytes> noteImages = noteImagesFile.stream()
                .map(i -> new ImageBytes(i.altText(), readFileBytes(i.file())))
                .toList();

        Faker faker = new Faker();

        for (int i = 0; i < 3; i++) {
            String firstName = faker.name().firstName();
            String lastName = faker.name().lastName();
            String username = (faker.letterify("??") + firstName.substring(0, 3) + lastName.substring(0, 3)).toLowerCase();
            String name = firstName + " " + lastName;
            String email = username + "@example.com";

            User newUser = new User(email, username, passwordEncoder.encode(username), name, List.of(userRole));
            File userImageFile = new File(IMAGES_DIRECTORY + "/user/" + i + ".jpg");
            byte[] userImageContent = readFileBytes(userImageFile);
            UserImage newUserImage = new UserImage(username, Files.probeContentType(userImageFile.toPath()), userImageContent, newUser);

            userRepository.save(newUser);
            userImageRepository.save(newUserImage);
            String noteContent = faker.lorem().paragraph();
            String noteTitle = faker.lorem().sentence();
            for (int j = 0; j < faker.number().numberBetween(1, 3); j++) {
                Note newNote = new Note(noteTitle, noteContent.substring(0, Math.min(100, noteContent.length())), newUser);
                byte[] newNoteImageContent = noteImages.get(j).file();
                NoteImage newNoteImage = new NoteImage(noteImagesFile.get(j)
                        .altText(), Files.probeContentType(noteImagesFile.get(j)
                        .file()
                        .toPath()), newNoteImageContent, newNote);
                noteRepository.save(newNote);
                noteImageRepository.save(newNoteImage);
            }

        }

        File ditodImageFile = new File(IMAGES_DIRECTORY + "/user/ditod.jpg");
        byte[] ditodImageContent = readFileBytes(ditodImageFile);
        User ditod = new User("ditod@example.com", "ditod", passwordEncoder.encode("123456"), "Orlando Diaz", List.of(adminRole));
        UserImage ditodImage = new UserImage("Dito's profile picture", Files.probeContentType(ditodImageFile.toPath()), ditodImageContent, ditod);

        Note ditodNote = new Note("Tiger", "Tigers are great", ditod);
        File ditodNoteImageFile = new File(IMAGES_DIRECTORY + "/ditod-notes/cute-koala.png");
        byte[] ditodNoteImageContent = readFileBytes(ditodNoteImageFile);
        NoteImage ditodNoteImage = new NoteImage("Cute looking koala", Files.probeContentType(ditodNoteImageFile.toPath()), ditodNoteImageContent, ditodNote);

        userRepository.save(ditod);
        userImageRepository.save(ditodImage);
        noteRepository.save(ditodNote);
        noteImageRepository.save(ditodNoteImage);
    }

    record ImageFile(String altText, File file) {
    }

    record ImageBytes(String altText, byte[] file) {
    }
}
