package com.ditod.notes.web.note_image.dto;

import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public class NoteImageRequest {
    private UUID id;
    private String altText;
    private MultipartFile file;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getAltText() {
        return altText;
    }

    public void setAltText(String altText) {
        this.altText = altText;
    }

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }

    @Override
    public String toString() {
        return "NoteImageRequest{" + "id=" + id + ", altText='" + altText + '\'' + ", file=" + file + '}';
    }
}
