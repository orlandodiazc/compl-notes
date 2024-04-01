package com.ditod.notes.domain.note;

import com.ditod.notes.domain.note.dto.NoteImageRequest;
import com.ditod.notes.domain.note_image.NoteImage;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class NoteService {
    public List<NoteImage> convertMultipartFilesToNoteImage(
            List<NoteImageRequest> images, Note note) {

        return Optional.ofNullable(images)
                .map(Collection::stream)
                .orElseGet(Stream::empty)
                .map(i -> {
                    try {
                        return new NoteImage(i.getId(), i.getAltText(), i.getFile()
                                .getContentType(), i.getFile()
                                .getBytes(), note);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .toList();
    }

}
