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
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.File;
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


        File ditodImageFile = new File(IMAGES_DIRECTORY + "/user/ditod.png");
        byte[] ditodImageContent = Files.readAllBytes(ditodImageFile.toPath());
        User ditod = new User("ditod@test.com", "ditod", passwordEncoder.encode("123456"), "Orlando Diaz", List.of(adminRole));
        UserImage ditodImage = new UserImage("Dito's profile picture", MediaType.IMAGE_PNG.toString(), ditodImageContent, ditod);

        Note ditodNote = new Note("Koalas", "Koalas are great", ditod);
        File ditodNoteImageFile = new File(IMAGES_DIRECTORY + "/ditod-notes/cute-koala.png");
        byte[] ditodNoteImageContent = Files.readAllBytes(ditodNoteImageFile.toPath());
        NoteImage ditodNoteImage = new NoteImage("Dito's note picture", MediaType.IMAGE_PNG.toString(), ditodNoteImageContent, ditodNote);


        User pedro = new User("pedro@test.com", "pedro", passwordEncoder.encode("123456"), "Pedro Patin", List.of(userRole));
        userRepository.save(pedro);
        User pablo = new User("pablo@test.com", "pablo", passwordEncoder.encode("123456"), "Pablo Barra", List.of(userRole));
        userRepository.save(pablo);

        userRepository.save(ditod);
        userImageRepository.save(ditodImage);
        noteRepository.save(ditodNote);
        noteImageRepository.save(ditodNoteImage);
    }
}
