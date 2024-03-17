package com.ditod.notes.domain.user_image;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

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
    ResponseEntity<?> one(@PathVariable UUID imageId) {
        var userImage = userImageRepository.findById(imageId);
        System.out.println("image: " + userImage);
        if (userImage.isEmpty()) {
            return ResponseEntity.status(400).body("Image Not Found");
        } else {
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.setContentType(MediaType.parseMediaType(userImage.get()
                    .getContentType()));
            responseHeaders.setContentLength(userImage.get().getBlob().length);
            responseHeaders.setContentDisposition(ContentDisposition.builder("inline")
                    .filename(imageId.toString())
                    .build());
            responseHeaders.setCacheControl(CacheControl.maxAge(Duration.ofDays(365))
                    .cachePublic()
                    .immutable());
            return ResponseEntity.ok()
                    .headers(responseHeaders)
                    .body(userImage.get().getBlob());

        }
    }


}
