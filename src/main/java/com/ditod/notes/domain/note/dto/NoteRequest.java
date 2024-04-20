package com.ditod.notes.domain.note.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public class NoteRequest {
    private String id;

    private @NotBlank(message = "Title is required") @Size(min = 1, max = 100, message = "Title length must be between 1 and 100 characters") String title;

    private @NotBlank(message = "Content is required") @Size(min = 1, max = 10000, message = "Content length must be between 1 and 10000 characters") String content;

    private @Size(max = 5, message = "The maximum number of images is 5") List<NoteImageRequest> images;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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
