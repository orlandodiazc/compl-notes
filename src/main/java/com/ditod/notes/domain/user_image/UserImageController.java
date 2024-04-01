package com.ditod.notes.domain.user_image;

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
    private final UserImageService userImageService;

    public UserImageController(UserImageService userImageService) {
        this.userImageService = userImageService;
    }

    @GetMapping("/{imageId}")
    ResponseEntity<?> oneUserImage(@PathVariable UUID imageId) {
        UserImage userImage = userImageService.findById(imageId);
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



