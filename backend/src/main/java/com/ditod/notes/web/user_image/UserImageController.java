package com.ditod.notes.web.user_image;

import com.ditod.notes.domain.exception.EntityDoesNotExistException;
import com.ditod.notes.domain.user.User;
import com.ditod.notes.domain.user.UserRepository;
import com.ditod.notes.domain.user_image.UserImage;
import com.ditod.notes.domain.user_image.UserImageRepository;
import com.ditod.notes.utils.ImageUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/user-images")
@Tag(name = "user image", description = "Access user images")
public class UserImageController {
    private final UserImageRepository userImageRepository;
    private final ImageUtils imageUtils;
    private final UserRepository userRepository;

    public UserImageController(UserImageRepository userImageRepository, ImageUtils imageUtils,
                               UserRepository userRepository) {
        this.userImageRepository = userImageRepository;
        this.imageUtils = imageUtils;
        this.userRepository = userRepository;
    }

    @GetMapping("/{imageId}")
    ResponseEntity<byte[]> getUserImage(@PathVariable UUID imageId) {
        UserImage userImage = userImageRepository.findById(imageId)
                                                 .orElseThrow(() -> new EntityDoesNotExistException("user image", imageId));

        return ResponseEntity.ok().headers(
                imageUtils.getImageResponseHeaders(userImage.getId(), userImage.getContentType(),
                                                   userImage.getBlob().length)).body(userImage.getBlob());
    }

    @PostMapping("/")
    public ResponseEntity<Object> updateOrCreateUserImage(@RequestParam("image") MultipartFile file,
                                                          Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        if (file.isEmpty()) {
            return ResponseEntity.badRequest()
                                 .body(ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "No file uploaded."));
        }
        if (!isValidImageType(file.getContentType())) {
            return ResponseEntity.badRequest()
                                 .body(ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Invalid image type."));
        }

        try {
            byte[] imageBlob = file.getBytes();

            Optional.ofNullable(user.getImage()).ifPresentOrElse(image -> {
                image.setBlob(imageBlob);
                image.setContentType(file.getContentType());
            }, () -> user.setImage(
                    new UserImage(user.getUsername() + "'s profile photo", file.getContentType(),
                                  imageBlob, null)));
            userRepository.save(user);
            return ResponseEntity.ok().build();
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing the file.");
        }
    }

    private boolean isValidImageType(@Nullable String contentType) {
        if (contentType == null) return false;
        return contentType.equals("image/jpeg") || contentType.equals("image/png");
    }

    @DeleteMapping("/{imageId}")
    ResponseEntity<Void> delete(@PathVariable UUID imageId, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        user.setImage(null);
        userRepository.save(user);
        return ResponseEntity.ok().build();
    }
}



