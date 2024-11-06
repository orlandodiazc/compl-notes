package com.ditod.notes.web.note_image;

import com.ditod.notes.domain.exception.EntityNotFoundException;
import com.ditod.notes.domain.note_image.NoteImage;
import com.ditod.notes.domain.note_image.NoteImageRepository;
import com.ditod.notes.utils.ImageUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;
import java.util.UUID;

@RestController
@RequestMapping("/note-images")
@Tag(name = "note image", description = "Access note images")
public class NoteImageController {
    private final NoteImageRepository noteImageRepository;
    private final ImageUtils imageUtils;

    public NoteImageController(NoteImageRepository noteImageRepository, ImageUtils imageUtils) {
        this.noteImageRepository = noteImageRepository;
        this.imageUtils = imageUtils;
    }

    @GetMapping("/{imageId}")
    ResponseEntity<byte[]> getNoteImage(@PathVariable UUID imageId) throws SQLException {
        NoteImage userImage = noteImageRepository.findById(imageId)
                                                 .orElseThrow(() -> new EntityNotFoundException("note image", imageId));

        return ResponseEntity.ok().headers(
                imageUtils.getImageResponseHeaders(userImage.getId(), userImage.getContentType(),
                                                   userImage.getBlob().length)).body(userImage.getBlob());
    }
}
