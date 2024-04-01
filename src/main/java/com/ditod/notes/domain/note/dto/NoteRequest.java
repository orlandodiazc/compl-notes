package com.ditod.notes.domain.note.dto;

import java.util.List;

public class NoteRequest {
    private String title;
    private String content;
    private List<NoteImageRequest> images;

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
