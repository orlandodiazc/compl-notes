package com.ditod.notes.web.user_image;

import com.ditod.notes.domain.exception.EntityDoesNotExistException;
import com.ditod.notes.domain.user.UserRepository;
import com.ditod.notes.domain.user_image.UserImage;
import com.ditod.notes.domain.user_image.UserImageRepository;
import com.ditod.notes.utils.ImageUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        UserImage userImage = userImageRepository.findById(imageId).orElseThrow(
                () -> new EntityDoesNotExistException("user image", imageId));

        return ResponseEntity.ok().headers(
                                     imageUtils.getImageResponseHeaders(userImage.getId(), userImage.getContentType(),
                                                                        userImage.getBlob().length))
                             .body(userImage.getBlob());
    }
}



