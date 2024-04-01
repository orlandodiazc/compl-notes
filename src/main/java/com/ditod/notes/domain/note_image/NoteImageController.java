package com.ditod.notes.domain.note_image;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.UUID;

@RestController
@RequestMapping("/note-images")
public class NoteImageController {
    private final NoteImageRepository noteImageRepository;

    public NoteImageController(NoteImageRepository noteImageRepository) {
        this.noteImageRepository = noteImageRepository;
    }

    @GetMapping("/{imageId}")
    ResponseEntity<?> oneNoteImage(@PathVariable UUID imageId) {
        var userImage = noteImageRepository.findById(imageId);
        if (userImage.isEmpty()) {
            return ResponseEntity.status(404).body("Image Not Found");
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
