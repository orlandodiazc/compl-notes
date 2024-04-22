package com.ditod.notes.domain.note_image;

import com.ditod.notes.domain.exception.EntityNotFoundException;
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
        NoteImage userImage = noteImageRepository.findById(imageId)
                .orElseThrow(() -> new EntityNotFoundException("note image", imageId));
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
