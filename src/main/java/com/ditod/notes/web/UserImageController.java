package com.ditod.notes.web;

import com.ditod.notes.domain.exception.EntityNotFoundException;
import com.ditod.notes.domain.user_image.UserImage;
import com.ditod.notes.domain.user_image.UserImageRepository;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.UUID;

@RestController
@RequestMapping("/user-images")
public class UserImageController {
    private final UserImageRepository userImageRepository;

    public UserImageController(UserImageRepository userImageRepository) {
        this.userImageRepository = userImageRepository;
    }

    @GetMapping("/{imageId}")
    ResponseEntity<?> oneUserImage(@PathVariable UUID imageId) {
        UserImage userImage = userImageRepository.findById(imageId)
                .orElseThrow(() -> new EntityNotFoundException("user image", imageId));
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.parseMediaType(userImage.getContentType()));
        responseHeaders.setContentLength(userImage.getBlob().length);
        responseHeaders.setContentDisposition(ContentDisposition.builder("inline")
                .filename(imageId.toString())
                .build());
        responseHeaders.setCacheControl(CacheControl.maxAge(Duration.ofDays(365))
                .cachePublic()
                .immutable());

        return ResponseEntity.ok()
                .headers(responseHeaders)
                .body(userImage.getBlob());
    }
}



