package com.ditod.notes.domain.note.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;

import java.util.List;

public class NoteRequest {
    @Size(min = 1, max = 10)
    private String title;

    @Size(min = 1, max = 10000)
    private String content;

    @Size(max = 5)
    private List<@Valid NoteImageRequest> images;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<NoteImageRequest> getImages() {
        return images;
    }

    public void setImages(List<NoteImageRequest> images) {
        this.images = images;
    }
}
